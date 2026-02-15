package processor

import munit.FunSuite

class CalcTaxSpec extends FunSuite {

  test("CalcTax returns only tax total for a discounted order") {
    val tax = CalcTax(TestFixture1.order)
    assertEqualsDouble(tax, TestFixture1.expectedTax, 0.01)
  }

  test("CalcTax returns only tax total for scenario 2") {
    val tax = CalcTax(TestFixture2.order)
    assertEqualsDouble(tax, TestFixture2.expectedTax, 0.01)
  }

  private def assertEqualsDouble(actual: Double, expected: Double, tol: Double): Unit = {
    assert(math.abs(actual - expected) <= tol, s"Expected $expected but got $actual")
  }
}
