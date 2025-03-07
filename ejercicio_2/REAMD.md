# Ejercicio 2

Descomprime el archivo `ejercicio_2.zip` anexo. Las modificaciones deben realizarse **únicamente** dentro del archivo `StudentAnalytics.java`.

**Restricciones:**
- No se pueden modificar las firmas (signatures) de los métodos públicos ni protegidos dentro de `StudentAnalytics`, ni eliminarlos.
- Puedes adicionar métodos según sea necesario.

**Tareas:**
- Implementar `StudentAnalytics.averageAgeOfEnrolledStudentsParallelStream` para que realice lo mismo que `averageAgeOfEnrolledStudentsImperative`, pero usando _streams paralelos_ de Java.
- Implementar `StudentAnalytics.mostCommonFirstNameOfInactiveStudentsParallelStream` para que realice lo mismo que `mostCommonFirstNameOfInactiveStudentsImperative`, usando _streams paralelos_.
- Implementar `StudentAnalytics.countNumberOfFailedStudentsOlderThan20ParallelStream` para que realice lo mismo que `countNumberOfFailedStudentsOlderThan20Imperative`, usando _streams paralelos_.

---

## Documentación de Java 8 Streams

Puedes consultar la [documentación oficial de Java 8](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html) sobre _Streams_. Además, aquí tienes un resumen introductorio:

### Introducción a Streams: Filter, Map y Reduce

Después de años evitando el estilo funcional, Java adoptó en 2014 las expresiones lambda y el API de Streams, lo cual permite trabajar con secuencias (streams) de elementos (listas, arrays, etc.) de una manera muy concisa.

**Ejemplo: Crear un stream a partir de una lista**

```java
// Crear una ArrayList
List<Integer> myList = new ArrayList<>();
myList.add(1);
myList.add(5);
myList.add(8);

// Convertir la lista en un Stream
Stream<Integer> myStream = myList.stream();
```

Si prefieres trabajar con arrays, también es muy sencillo:

```java
// Crear un array
Integer[] myArray = {1, 5, 8};

// Convertir el array en un Stream
Stream<Integer> myStream = Arrays.stream(myArray);
```

---

## Operaciones con Streams

### El Método `map`

El método `map` transforma cada elemento del stream aplicando una función (usualmente expresada con una lambda) y retorna un nuevo stream con los elementos transformados.

**Ejemplo:** Convertir un array de `String` a mayúsculas.

```java
String[] myArray = {"bob", "alice", "paul", "ellie"};
Stream<String> myStream = Arrays.stream(myArray);
Stream<String> myNewStream = myStream.map(s -> s.toUpperCase());
String[] myNewArray = myNewStream.toArray(String[]::new);
```

---

### El Método `filter`

`filter` permite obtener solo los elementos del stream que cumplan una determinada condición (la lambda debe devolver un booleano).

**Ejemplo:** Filtrar las cadenas cuya longitud sea mayor a 4:

```java
String[] myArray = {"apple", "dog", "banana", "cat"};
String[] result = Arrays.stream(myArray)
      .filter(s -> s.length() > 4)
      .toArray(String[]::new);
```

---

### Operaciones de Reducción

Las operaciones de reducción (terminales) permiten condensar los elementos del stream en un único resultado.  
Un ejemplo muy común es `toArray()`, pero también existen métodos como `sum()`, `average()`, y el método general `reduce()`.

**Ejemplo:** Sumar los elementos de un array de enteros:

```java
int[] myArray = {1, 5, 8};
int sum = Arrays.stream(myArray).sum();
```

Para operaciones más complejas, `reduce` permite definir un elemento identidad y una función que combine elementos:

```java
String[] myArray = {"this", "is", "a", "sentence"};
String result = Arrays.stream(myArray)
                .reduce("", (a, b) -> a + b);
```

---

## Streams Paralelos

Si tienes operaciones computacionalmente intensivas o streams muy grandes, considera usar streams paralelos. Es muy sencillo: solo llama al método `parallel()` en el stream.

```java
double average = students.parallelStream()
                         .mapToInt(Student::getAge)
                         .average()
                         .orElse(0);
```

También puedes utilizar _method references_ en lugar de expresiones lambda para mayor claridad.

---

**Conclusión:**

El API de Streams te permite escribir código más conciso y legible, eliminando la necesidad de bucles explícitos para transformaciones, filtrados y reducciones de colecciones. Experimenta con estas operaciones para obtener el máximo rendimiento y legibilidad en tus implementaciones.

# Desarrrollo

## Setup

```powershell
cd ejercicio_2
```

```powershell
mvn clean install '-Dmaven.test.skip=true'
```

## Pruebas

```powershell
mvn clean
mvn test '-Dtest=StudentAnalyticsTest#testAverageAgeOfEnrolledStudentsPerf' > ("testAverageAgeOfEnrolledStudentsPerf" + [DateTimeOffset]::Now.ToUnixTimeSeconds() + ".txt")
```