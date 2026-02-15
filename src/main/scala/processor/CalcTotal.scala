package processor

import domain.*

object CalcTotal {
  def apply(order: Order): Double = {
    val processedItems = DiscountCalculator.applyAll(order)
    val subtotal = processedItems.map(_.finalPrice).sum
    val totalTax = processedItems.map(i => i.finalPrice * i.taxRate).sum
    subtotal + totalTax
  }
}
