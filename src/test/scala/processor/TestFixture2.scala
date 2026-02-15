package processor

import java.time.LocalDate
import domain.*
import domain.DiscountRule.*
import domain.DiscountTarget.*

object TestFixture2 {
  val apple: Item = Item("Apple", 100, Category.Food)
  val laptop: Item = Item("Laptop", 1000, Category.Other)

  // Apple 3個 + Laptop 1個
  val items: List[Item] = List(apple, apple, apple, laptop)

  // 現在の実装ベースの割引適用ルール:
  // 1) TotalOrder の 10%引きは全アイテムに適用される
  // 2) QuantityThreshold("Apple", 3, AmountOff(5)) は Apple が3個以上あると
  //    Apple「各1個」に 5円引きが適用される（セット1回ではない）
  // 3) ComboDiscount(Set("Apple","Laptop"), 2, AmountOff(10)) は
  //    対象アイテム数が閾値以上なら、対象集合内の「各アイテム」に 10円引きが適用される
  //    （Apple3個とLaptop1個なら、Apple3個すべてとLaptop1個に適用）
  val discountRules: List[DiscountApplication] = List(
    DiscountApplication(PercentOff(10.0), TotalOrder),
    DiscountApplication(
      QuantityThreshold("Apple", 3, AmountOff(5.0)),
      SpecificItems(Set("Apple"))
    ),
    DiscountApplication(
      ComboDiscount(Set("Apple", "Laptop"), 2, AmountOff(10.0)),
      SpecificItems(Set("Apple", "Laptop"))
    )
  )

  // 持ち帰り時の税率を TaxPolicy から導出（Food 8%, Other 10%）
  val taxRules: List[TaxApplication] = TaxPolicy.taxRules(
    TaxContext(
      serviceType = ServiceType.Takeout,
      at = LocalDate.of(2026, 2, 15)
    )
  )

  val order: Order = Order(items, discountRules, taxRules)

  // 現在実装での期待値:
  // Apple: 100 -> 90 (10%引き) -> 85 (しきい値割引) -> 75 (コンボ割引) を3個
  // Laptop: 1000 -> 900 (10%引き) -> 890 (コンボ割引) を1個
  val expectedSubtotal: Double = 75 * 3 + 890
  val expectedTax: Double = (75 * 3) * 0.08 + 890 * 0.10
  val expectedTotal: Double = expectedSubtotal + expectedTax
}
