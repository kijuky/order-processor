package processor

import java.time.LocalDate
import domain.*
import domain.DiscountRule.*
import domain.DiscountTarget.*

object TestFixture1 {
  val apple: Item = Item("Apple", 100, Category.Food)
  val banana: Item = Item("Banana", 150, Category.Food)
  val laptop: Item = Item("Laptop", 1000, Category.Other)

  // りんご、バナナ、ノートPCを一つずつ購入
  val items: List[Item] = List(apple, banana, laptop)

  // 合計金額から10%割引
  val discountRules: List[DiscountApplication] = List(
    DiscountApplication(
      PercentOff(10.0),
      TotalOrder
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

  val expectedSubtotal: Double = 100 * 0.9 + 150 * 0.9 + 1000 * 0.9
  val expectedTax: Double = (100 * 0.9 + 150 * 0.9) * 0.08 + (1000 * 0.9) * 0.10
  val expectedTotal: Double = expectedSubtotal + expectedTax
}
