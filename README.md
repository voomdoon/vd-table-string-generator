# vd-table-string-generator

[![CI](https://github.com/voomdoon/vd-table-string-generator/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/voomdoon/vd-table-string-generator/actions/workflows/ci.yml?query=branch%3Amain)
[![Trivy](https://github.com/voomdoon/vd-table-string-generator/actions/workflows/trivy.yml/badge.svg)](https://github.com/voomdoon/vd-table-string-generator/actions/workflows/trivy.yml)
[![CodeQL Advanced](https://github.com/voomdoon/vd-table-string-generator/actions/workflows/codeql.yml/badge.svg)](https://github.com/voomdoon/vd-table-string-generator/actions/workflows/codeql.yml)
[![License](https://img.shields.io/github/license/voomdoon/vd-table-string-generator)](https://github.com/voomdoon/vd-table-string-generator/blob/main/LICENSE)
[![Java](https://img.shields.io/badge/Java-21-blue)](https://adoptium.net/)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=voomdoon_vd-table-string-generator&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=voomdoon_vd-table-string-generator)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=voomdoon_vd-table-string-generator&metric=coverage)](https://sonarcloud.io/summary/new_code?id=voomdoon_vd-table-string-generator)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=voomdoon_vd-table-string-generator&metric=bugs)](https://sonarcloud.io/summary/new_code?id=voomdoon_vd-table-string-generator)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=voomdoon_vd-table-string-generator&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=voomdoon_vd-table-string-generator)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=voomdoon_vd-table-string-generator&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=voomdoon_vd-table-string-generator)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=voomdoon_vd-table-string-generator&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=voomdoon_vd-table-string-generator)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=voomdoon_vd-table-string-generator&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=voomdoon_vd-table-string-generator)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=voomdoon_vd-table-string-generator&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=voomdoon_vd-table-string-generator)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=voomdoon_vd-table-string-generator&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=voomdoon_vd-table-string-generator)

A Java utility to generate formatted string representations of tables from a matrix of `String` values.

## Features
- Configurable column separator
- Custom null value representation
- Alignment for numbers and text
- Builder-based configuration

## Usage

### Basic Example

```java
import de.voomdoon.util.tostring.table.TableStringGenerator;

String[][] data = {
    {"a", "b", "c"}
};
String[] headline = {"A", "B", "C"};
String result = TableStringGenerator.DEFAULT.toString(data, headline);
System.out.println(result);
```

Output:
```
A │ B │ C
──┼───┼──
a │ b │ c
```

### Custom Column Separator

```java
TableStringGenerator generator = TableStringGenerator.builder()
    .setColumnSeparator(":")
    .build();
String[][] data = { {"a", "b"} };
String[] headline = {"A", "B"};
System.out.println(generator.toString(data, headline));
```

Output:
```
A:B
─:─
a:b
```

### Custom Null Value

```java
TableStringGenerator generator = TableStringGenerator.builder()
    .setNullValue("NULL")
    .build();
String[][] data = { {"test", null} };
System.out.println(generator.toString(data));
```

Output:
```
test │ NULL
```

### Padding and Alignment

```java
String[][] data = { {"aa", "b"} };
String[] headline = {"A", "B"};
System.out.println(TableStringGenerator.DEFAULT.toString(data, headline));
```

Output:
```
A  │ B
───┼──
aa │ b
```

### Numbers and Alignment

```java
String[][] data = { {"1", "2"}, {"11", "2"} };
System.out.println(TableStringGenerator.DEFAULT.toString(data));
```

Output:
```
 1 │ 2
11 │ 2
```
