package domain

import java.time.LocalDate
import munit.FunSuite

class TaxPolicySpec extends FunSuite {

  test("Takeout uses Food 8% and Other 10%") {
    val ctx = TaxContext(
      serviceType = ServiceType.Takeout,
      at = LocalDate.of(2026, 2, 15)
    )

    assertEquals(
      TaxPolicy.taxRules(ctx),
      List(
        TaxApplication(Category.Food, 0.08),
        TaxApplication(Category.Other, 0.10)
      )
    )
  }

  test("EatIn uses Food 10% and Other 10%") {
    val ctx = TaxContext(
      serviceType = ServiceType.EatIn,
      at = LocalDate.of(2026, 2, 15)
    )

    assertEquals(
      TaxPolicy.taxRules(ctx),
      List(
        TaxApplication(Category.Food, 0.10),
        TaxApplication(Category.Other, 0.10)
      )
    )
  }

  test("FoodTaxHoliday ignores ServiceType while active") {
    val ctx = TaxContext(
      serviceType = ServiceType.EatIn,
      at = LocalDate.of(2026, 7, 1),
      policyEvent = PolicyEvent.FoodTaxHoliday(
        start = LocalDate.of(2026, 1, 1),
        end = LocalDate.of(2027, 12, 31)
      )
    )

    assertEquals(
      TaxPolicy.taxRules(ctx),
      List(
        TaxApplication(Category.Food, 0.00),
        TaxApplication(Category.Other, 0.10)
      )
    )
  }

  test("FoodTaxHoliday outside period falls back to ServiceType rules") {
    val ctx = TaxContext(
      serviceType = ServiceType.EatIn,
      at = LocalDate.of(2028, 1, 1),
      policyEvent = PolicyEvent.FoodTaxHoliday(
        start = LocalDate.of(2026, 1, 1),
        end = LocalDate.of(2027, 12, 31)
      )
    )

    assertEquals(
      TaxPolicy.taxRules(ctx),
      List(
        TaxApplication(Category.Food, 0.10),
        TaxApplication(Category.Other, 0.10)
      )
    )
  }
}
