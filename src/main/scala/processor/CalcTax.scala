package processor

import domain.*

object CalcTax {
  def apply(order: Order): Double = {
    val processedItems = DiscountCalculator.applyAll(order)
    processedItems.map(i => i.finalPrice * i.taxRate).sum
  }
}
