# vd-table-string-generator

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
