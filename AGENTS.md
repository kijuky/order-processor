# Agents Guide for Order Processing Project

## 目的
このプロジェクトでは、AIがOrderドメインモデルを理解・操作できる設計を意識しています。  
- データは純粋（副作用なし）  
- 割引・税ルールは外部化  
- 中間状態 `ItemWithDiscountWithTax` で計算結果を明示  
- 税率は `TaxPolicy` で文脈（提供方法・時点・政策イベント）から導出可能  

## LLMが扱いやすい設計ポイント

1. **構造化されたデータ**  
   - `Order(items, discountRules, taxRules)` がトップレベル  
   - LLMは「Itemとルールが与えられた状態」を直接評価可能  
   - `TaxContext(serviceType, at, policyEvent)` により税率決定の文脈も明示可能  

2. **Interpreterパターン風の処理**  
   - processor群が純関数で評価  
   - `CalcTotal`, `CalcTax`, `ShowOrder` が独立している  
   - 税率は `TaxPolicy.taxRules` で `TaxContext` から決定する  

3. **AST的要素**
   - ドメインモデルはASTではないが、**AST的に評価可能なツリー構造**  
   - 割引ルールや税ルールを個別ノードとして処理できる  

4. **生成やテストの容易さ**
   - 各関数が独立しているため、LLMは部分的にコード生成やテスト用の例を作成しやすい  
   - 中間状態の明示により、税額や割引額の検証も容易  
   - `TaxPolicySpec` により、通常税率と時限政策税率を分離して検証しやすい  

## 拡張案
- ShowOrderのフォーマットを変更してレシート風に表示  
- 新しい割引ルールや税ルールを追加しても、既存の純関数処理に影響しにくい  
- `TaxPolicy` を期間ベースの履歴テーブル化し、過去/将来の税率改定を同じ仕組みで扱う  
- FP型クラスとして `OrderEvaluator[F[_]]` を実装すれば、任意のコンテキストで計算可能  
