package kinesis.mock.cache

import cats.effect.{Blocker, IO}
import cats.syntax.all._
import org.scalacheck.Test
import org.scalacheck.effect.PropF

import kinesis.mock.api._
import kinesis.mock.instances.arbitrary._
import kinesis.mock.models._

class DisableEnhancedMonitoringTests
    extends munit.CatsEffectSuite
    with munit.ScalaCheckEffectSuite {

  override def scalaCheckTestParameters: Test.Parameters =
    Test.Parameters.default.withMinSuccessfulTests(5)

  test("It should add disable enhanced monitoring")(PropF.forAllF {
    (
      streamName: StreamName
    ) =>
      Blocker[IO].use(blocker =>
        for {
          cacheConfig <- CacheConfig.read(blocker)
          cache <- Cache(cacheConfig)
          _ <- cache.createStream(CreateStreamRequest(1, streamName)).rethrow
          _ <- cache
            .enableEnhancedMonitoring(
              EnableEnhancedMonitoringRequest(
                List(ShardLevelMetric.ALL),
                streamName
              )
            )
            .rethrow
          res <- cache
            .disableEnhancedMonitoring(
              DisableEnhancedMonitoringRequest(
                List(ShardLevelMetric.IncomingBytes),
                streamName
              )
            )
            .rethrow
          streamMonitoring <- cache
            .describeStreamSummary(DescribeStreamSummaryRequest(streamName))
            .rethrow
            .map(
              _.streamDescriptionSummary.enhancedMonitoring
                .flatMap(_.shardLevelMetrics)
            )
        } yield assert(
          res.desiredShardLevelMetrics == streamMonitoring && !res.desiredShardLevelMetrics
            .exists(_ == ShardLevelMetric.IncomingBytes)
        )
      )
  })
}