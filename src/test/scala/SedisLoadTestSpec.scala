import java.util
import java.util.Date
import java.util.logging.Logger

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers._
import org.sedis.Pool
import redis.clients.jedis._

import scala.concurrent.Future
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global

class SedisLoadTestSpec extends FunSpec{
  describe("A Scala redis server") {
    val pool = new Pool(new JedisPool(new JedisPoolConfig(), "localhost", 6379, 2000, null, 4))
    val j = pool.underlying.getResource
    j.flushAll
    pool.underlying.returnResourceObject(j)

    it("loading test") {
      (1 to 100).map{i =>
        Future{random(i)}
      }

      Thread.sleep(1000)

    }

    def random(index:Int): Unit ={
      pool.withClient { client =>
        Console.println(s"random(${String.format("%3d",new Integer(index))}) at ${new Date().getTime()} in Thread${Thread.currentThread().getId}")
        (1 to 1000).map{n=>
          client.set(s"Int-$n", ""+Random.nextInt()) should be ("OK")
          client.set(s"Boolean-$n", ""+ Random.nextBoolean()) should be ("OK")
          client.set(s"Double-$n", "" +Random.nextDouble()) should be ("OK")
          client.set(s"Float-$n", ""+ Random.nextFloat()) should be ("OK")
          client.set(s"Long-$n", ""+ Random.nextLong()) should be ("OK")
          client.set(s"String-$n", ""+ Random.nextString(Random.nextInt(100))) should be ("OK")
          client.rpush(s"Set", s"$n") shouldBe > (0)
        }
      }
    }
  }

}
// vim: set ts=4 sw=4 et:
