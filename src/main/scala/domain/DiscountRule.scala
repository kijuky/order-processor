package domain

enum DiscountRule:
  case PercentOff(percent: Double)
  case AmountOff(amount: Double)
  case QuantityThreshold(itemName: String, minQty: Int, discount: DiscountRule)
  case ComboDiscount(items: Set[String], requiredQty: Int, discount: DiscountRule.AmountOff)

enum DiscountTarget:
  case TotalOrder
  case SpecificItems(itemNames: Set[String])

final case class DiscountApplication(rule: DiscountRule, target: DiscountTarget)
