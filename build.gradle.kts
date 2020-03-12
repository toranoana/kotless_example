import io.kotless.DSLType
import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless

group = "org.example"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.50" apply true
    // Kotlessプラグインを有効化
    id("io.kotless") version "0.1.2" apply true
}

repositories {
    jcenter()
}

dependencies {
    // Kotless Ktor DSL Library
    implementation("io.kotless", "ktor-lang", "0.1.2")
    // AWS DynamoDB SDK
    implementation("com.amazonaws", "aws-java-sdk-dynamodb", "1.11.650")
    // Ktor HTML-Builder Feature
    implementation("io.ktor", "ktor-html-builder", "1.2.5")
}

kotless {
    config {
        // 利用するバケット名を設定
        bucket = "kotless.example.com"

        dsl {
            type = DSLType.Ktor
        }

        // Terraformで利用するプロファイルとリージョンを設定
        terraform {
            profile = "default"
            region = "ap-northeast-1"
        }
    }

    webapp {
        // Webアプリケーションを公開するRoute53のエイリアスを設定
        route53 = Route53("kotless", "example.com")
    }

    extensions {
        // ローカル実行時の設定
        local {
            port = 9090
        }
    }
}