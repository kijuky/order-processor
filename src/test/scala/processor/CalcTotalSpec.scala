package processor

import munit.FunSuite

class CalcTotalSpec extends FunSuite {

  test("CalcTotal returns subtotal + tax for a discounted order") {
    val total = CalcTotal(TestFixture1.order)
    assertEqualsDouble(total, TestFixture1.expectedTotal, 0.01)
  }

  test("CalcTotal returns subtotal + tax for scenario 2") {
    val total = CalcTotal(TestFixture2.order)
    assertEqualsDouble(total, TestFixture2.expectedTotal, 0.01)
  }

  private def assertEqualsDouble(actual: Double, expected: Double, tol: Double): Unit = {
    assert(math.abs(actual - expected) <= tol, s"Expected $expected but got $actual")
  }
}
