package processor

import domain.*
import domain.DiscountRule.*
import domain.DiscountTarget.*

object DiscountCalculator {

  private def applyDiscount(item: Item, order: Order): ItemWithDiscountWithTax = {
    val finalPrice = order.discountRules.foldLeft(item.price) { (price, discountApp) =>
      val applies = discountApp.target match {
        case TotalOrder => true
        case SpecificItems(names) => names.contains(item.name)
      }

      if (!applies) price
      else discountApp.rule match {
        case PercentOff(p) => price * (1 - p / 100)
        case AmountOff(a) => price - a
        case QuantityThreshold(name, minQty, r) =>
          val count = order.items.count(_.name == name)
          if (item.name == name && count >= minQty)
            applyDiscount(item.copy(price = price), order.copy(discountRules = List(DiscountApplication(r, SpecificItems(Set(name)))))).finalPrice
          else price
        case ComboDiscount(combo, reqQty, AmountOff(a)) =>
          val count = order.items.count(i => combo.contains(i.name))
          if (combo.contains(item.name) && count >= reqQty) price - a
          else price
      }
    }

    val discountApplied = item.price - finalPrice
    val taxRate = order.taxRules.find(_.category == item.category).map(_.rate).getOrElse(0.0)

    ItemWithDiscountWithTax(item, discountApplied, finalPrice, taxRate)
  }

  def applyAll(order: Order): List[ItemWithDiscountWithTax] =
    order.items.map(item => applyDiscount(item, order))
}

private final case class ItemWithDiscountWithTax(
    item: Item,
    discountApplied: Double, // 割引額
    finalPrice: Double,      // 割引後価格
    taxRate: Double          // 適用税率
)
