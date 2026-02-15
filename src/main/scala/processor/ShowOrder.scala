package processor

import domain.*

object ShowOrder {
  def apply(order: Order): String = {
    val processedItems = DiscountCalculator.applyAll(order)
    val sb = new StringBuilder
    sb.append("===== RECEIPT =====\n")
    processedItems.foreach { i =>
      sb.append(f"${i.item.name}  ${i.item.price} -> ${i.finalPrice} (Discount: ${i.discountApplied})  TaxRate: ${i.taxRate * 100}%%\n")
    }
    val subtotal = processedItems.map(_.finalPrice).sum
    val totalTax = processedItems.map(i => i.finalPrice * i.taxRate).sum
    sb.append(f"Subtotal: $$${subtotal}%.2f\n")
    sb.append(f"Tax:      $$${totalTax}%.2f\n")
    sb.append(f"Total:    $$${subtotal + totalTax}%.2f\n")
    sb.append("===================\n")
    sb.toString()
  }
}
