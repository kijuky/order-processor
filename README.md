# Order Processing Project (Scala / SBT)

## 概要
このプロジェクトは、Scala + FPスタイルで「注文（Order）の合計・税額・割引・レシート表示」を管理する学習用サンプルです。  
ドメインモデルを純粋データとして定義し、処理は `processor` パッケージの純関数で行います。  
ASTっぽく構造化されたドメインモデルを評価するイメージで設計しています。

### パッケージ構成

```
src/main/scala/
├─ domain/
│   ├─ Category.scala
│   ├─ Item.scala
│   ├─ DiscountRule.scala
│   ├─ TaxRule.scala
│   └─ Order.scala
└─ processor/
    ├─ DiscountCalculator.scala
    ├─ CalcTotal.scala
    ├─ CalcTax.scala
    └─ ShowOrder.scala

src/test/scala/processor/
├─ TestFixture1.scala
├─ TestFixture2.scala
├─ CalcTotalSpec.scala
├─ CalcTaxSpec.scala
└─ ShowOrderSpec.scala

src/test/scala/domain/
└─ TaxPolicySpec.scala
```

- **domain**: ドメインモデル（Order, Item, DiscountRule, TaxRule）  
- **processor**: Orderを処理する純関数群（合計計算、税計算、レシート表示）

### 機能

- 割引ルール適用（割合・定額・セット割引・特定アイテム割引）  
- 税計算（カテゴリ別税率 + 提供方法 + 時限ポリシー）  
- Order合計計算（税込み合計・税額取得）  
- レシート表示（ShowOrder）

### 設計意図（LLM/FP観点）

- `Order(items, discountRules, taxRules)` に評価に必要な情報を集約し、入力構造を明確化  
- `DiscountCalculator` / `CalcTax` / `CalcTotal` / `ShowOrder` を純関数として分離し、処理責務を局所化  
- 割引は「注文全体の1回配賦」ではなく、**各商品に対する減額額を先に確定**してから税計算する方針  
- この方針により、異なる税率カテゴリ（例: Food 8%, Other 10%）が混在しても税額を一貫して計算可能
- 税率は `TaxPolicy.taxRules(TaxContext)` から導出可能にし、`ServiceType` や `PolicyEvent`（時限減税など）を切り替え可能

### 割引と税計算の考え方

- `DiscountCalculator` は各 `Item` に対して割引後価格と税率を確定する  
- `CalcTax` は確定後価格に税率を掛けて合計税額を算出する  
- `CalcTotal` は割引後小計 + 税額で合計を算出する  
- `ShowOrder` は上記結果をレシート形式で表示する

### 税率ポリシー

- `ServiceType`:
  - `Takeout`（持ち帰り）
  - `EatIn`（イートイン）
- `PolicyEvent`:
  - `None`
  - `FoodTaxHoliday(start, end)`（食料品税率を期間限定で0%にする特例）
- 優先順位:
  - `FoodTaxHoliday` が `at` に対して有効なら、`ServiceType` を無視して `Food=0%`, `Other=10%`
  - それ以外は `ServiceType` ごとの通常税率を適用

### 使用方法

1. SBT プロジェクトとして開く  
2. `Order` と `Item` を作成  
3. 各 processor を直接呼び出して計算・表示  

```scala
import domain.*
import processor.*

val apple = Item("Apple", 100, Category.Food)
val banana = Item("Banana", 150, Category.Food)
val items = List(apple, banana)
val discountRules = List( /* 割引ルール */ )

val taxContext = TaxContext(
  serviceType = ServiceType.Takeout,
  at = java.time.LocalDate.of(2026, 2, 15)
)
val taxRules = TaxPolicy.taxRules(taxContext)

val order = Order(items, discountRules, taxRules)

val total = CalcTotal(order)
val tax = CalcTax(order)
val receipt = ShowOrder(order)
```

`FoodTaxHoliday` を適用する例:

```scala
val holidayContext = TaxContext(
  serviceType = ServiceType.EatIn, // Holiday中は無視される
  at = java.time.LocalDate.of(2026, 7, 1),
  policyEvent = PolicyEvent.FoodTaxHoliday(
    start = java.time.LocalDate.of(2026, 1, 1),
    end = java.time.LocalDate.of(2027, 12, 31)
  )
)

val holidayTaxRules = TaxPolicy.taxRules(holidayContext)
// Food = 0.00, Other = 0.10
```
