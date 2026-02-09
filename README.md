# BoardGame App

ログイン制のボードゲーム管理アプリ。BoardGameGeek API でゲーム情報を取得し、マイページで所持ゲームのリスト管理とプレイ記録ができます。

## 技術スタック

- **バックエンド**: Java 17, Spring Boot 3, Spring Security + JWT, Spring Data JPA, H2
- **フロントエンド**: Vue 3, TypeScript, Vite, Vue Router, Pinia, Axios

## 必要な環境

- Java 17 以上
- Maven 3.6 以上
- Node.js 18 以上

## 起動方法

### バックエンド

```bash
cd java
mvn spring-boot:run
```

デフォルトで http://localhost:8080 で起動します。

### フロントエンド

```bash
cd Vue
npm install
npm run dev
```

http://localhost:5173 で開きます。API は Vite のプロキシで 8080 に転送されます。

## 機能

- ユーザー登録・ログイン（JWT）
- 所持ゲームの追加（BGG 検索でゲームを選んで追加）
- 所持ゲーム一覧・削除
- 各ゲームにプレイ記録（日付・人数・メモ）を追加・表示
