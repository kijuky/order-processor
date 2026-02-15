package processor

import munit.FunSuite

class ShowOrderSpec extends FunSuite {

  test("ShowOrder renders receipt lines and totals") {
    val receipt = ShowOrder(TestFixture1.order)

    assert(receipt.contains("Apple"))
    assert(receipt.contains("Laptop"))
    assert(receipt.contains(f"Subtotal: $$${TestFixture1.expectedSubtotal}%.2f"))
    assert(receipt.contains(f"Tax:      $$${TestFixture1.expectedTax}%.2f"))
    assert(receipt.contains(f"Total:    $$${TestFixture1.expectedTotal}%.2f"))
  }

  test("ShowOrder renders receipt lines and totals for scenario 2") {
    val receipt = ShowOrder(TestFixture2.order)

    assert(receipt.contains("Apple"))
    assert(receipt.contains("Laptop"))
    assert(receipt.contains(f"Subtotal: $$${TestFixture2.expectedSubtotal}%.2f"))
    assert(receipt.contains(f"Tax:      $$${TestFixture2.expectedTax}%.2f"))
    assert(receipt.contains(f"Total:    $$${TestFixture2.expectedTotal}%.2f"))
  }
}
