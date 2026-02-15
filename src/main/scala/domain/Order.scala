package domain

final case class Order(
    items: List[Item] = List(),
    discountRules: List[DiscountApplication] = List(),
    taxRules: List[TaxApplication] = List()
)
