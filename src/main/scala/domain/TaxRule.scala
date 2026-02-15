package domain

import java.time.LocalDate

case class TaxApplication(category: Category, rate: Double)

enum ServiceType:
  case Takeout
  case EatIn

enum PolicyEvent:
  case None
  case FoodTaxHoliday(start: LocalDate, end: LocalDate)

final case class TaxContext(
    serviceType: ServiceType,
    at: LocalDate,
    policyEvent: PolicyEvent = PolicyEvent.None
)

object TaxPolicy {
  def taxRules(ctx: TaxContext): List[TaxApplication] = {
    if (isFoodTaxHolidayActive(ctx.policyEvent, ctx.at)) {
      List(
        TaxApplication(Category.Food, 0.00),
        TaxApplication(Category.Other, 0.10)
      )
    } else {
      ctx.serviceType match {
        case ServiceType.Takeout =>
          List(
            TaxApplication(Category.Food, 0.08),
            TaxApplication(Category.Other, 0.10)
          )
        case ServiceType.EatIn =>
          List(
            TaxApplication(Category.Food, 0.10),
            TaxApplication(Category.Other, 0.10)
          )
      }
    }
  }

  private def isFoodTaxHolidayActive(policyEvent: PolicyEvent, at: LocalDate): Boolean =
    policyEvent match {
      case PolicyEvent.FoodTaxHoliday(start, end) =>
        !at.isBefore(start) && !at.isAfter(end)
      case PolicyEvent.None =>
        false
    }
}
