# Ejercicio 1: Paralelismo basado en tareas (Fork-Join)

Para el ejercicio_1 se debe utilizar el framework Fork Join de Java para escribir una implementación paralela de la suma de los recíprocos de cada elemento de un arreglo. 

El cálculo de la suma de los recíprocos de un arreglo implica sumar todos los elementos del arreglo. El recíproco de un valor v es 1/v. 

El siguiente seudocódigo ilustra una manera secuencial para calcular la suma de los recíprocos de un arreglo A:

``` 
sum = 0
for v in A:
    sum = sum + (1 / v) 
print ‘The reciprocal array sum of the input array is ‘ + sum
```

Es importante observar que el cálculo del recíproco en cada paso de la iteración es independiente del cálculo del recíproco de cualquier otra iteración, de tal forma que el problema puede paralelizarse. En particular el grafo computacional, para un arreglo con cuatro elementos puede ser visualizado como

Donde el círculo rojo indica el inicio del programa paralelo, el círculo verde la finalización y cada círculo azul representa una iteración del ciclo for.
La meta de este proyecto es utilizar el framework para fork-join de Java para paralelizar la implementación secuencial proporcionada.
Configuración del proyecto

Por favor, vea la documentación del ejercicio_0 para una descripción del proceso para construir y hacer las pruebas.
Descomprima el archivo ejercicio_1.zip anexo. Debe revisar el código fuente de 
Ejercicio_1/src/main/java/co/edu/unal/paralela/𝚁𝚎𝚌𝚒𝚙𝚛𝚘𝚌𝚊𝚕𝙰𝚛𝚛𝚊𝚢𝚂𝚞𝚖.𝚓𝚊v𝚊
Y la prueba del proyecto
Ejercicio_1/src/main/java/co/edu/unal/paralela/𝚁𝚎𝚌𝚒𝚙𝚛𝚘𝚌𝚊𝚕𝙰𝚛𝚛𝚊𝚢𝚂𝚞𝚖𝚃𝚎𝚜𝚝.𝚓𝚊𝚟𝚊

## Instrucciones

Las modificaciones deben realizarse únicamente dentro del archivo ReciprocalArraySum.java. NO se deben cambiar las ‘signatures’ de los métodos públicos (public) ni protegidos (protected) dentro de ReciprocalArraySum.java, pero se pueden editar el cuerpo de los métodosy adicional nuevos métodos si lo considera. 

## Actividades a realizar

Modificar el método ReciprocalArraySum.parArraySum() para implementar el cálculo de la suma de los recíprocos de un arreglo utilizando el framework Fork Join de Java mediante la partición del arreglo de entrada a la mitad y calculando la suma de los recíprocos del arreglo a partir de la suma de la primera parte y de la segunda parte en paralelo, antes de combinar los resultados. Hay varias actividades “Para hacer” (TODOs) en el código fuente como guía. 

La siguiente actividad es una generalizacoón de la primera, así que ya se debe tener confianza para particionar el trabajo entre múltiples tareas Fork-Join (no solo entre dos tareas). 

Observe que para realizar esta actividad y la siguiente se puede tener una ForkjoinPool dentro de parArraySum() y de parManyTaskArraySum() para ejecutar dentro las diferentes tareas. Por ejemplo, para crear una ForkJoinPool con 2 hilosse requiere el siguiente código:

```java
import java.util.concurrent.ForkJoinPool;
ForkJoinPool pool = new ForkJoinPool(2);
```
  
Modificar el método ReciprocalArraySum.parManyTaskArraySum para implementar el cálculo de la suma de los recíprocos de un arreglo en paralelo utilizando el framework Fork Join de Java nuevamente, pero esta vez se debe usar un número de tareas estipulado (no solo 2 tareas como en la actividad 1). Observe que los métodos getChunkStartInclusive y getChunkEndExclusive se presentan aquí para ayudar a calcular la región del arreglo de entrada que cierta tarea debe procesar.

# Desarrollo

## Setup

```powershell
cd ejercicio_1
```

```powershell
mvn clean install '-Dmaven.test.skip=true'
```

Mostra cantidad de núcleos de la máquina

```powershell
wmic cpu get NumberOfLogicalProcessors
```

```bash
nproc
```

Mostrar la cantidad de preocesadores en la JVM

```powershell
echo "System.out.println(Runtime.getRuntime().availableProcessors());" | jshell -q
```

```bash
echo "System.out.println(Runtime.getRuntime().availableProcessors());" | jshell -q
```

## Pruebas


```powershell
mvn clean
mvn test '-Dtest=ReciprocalArraySumTest#testParSimpleTwoMillion' > ("testParSimpleTwoMillion" + [DateTimeOffset]::Now.ToUnixTimeSeconds() + ".txt")
mvn test '-Dtest=ReciprocalArraySumTest#testParSimpleTwoHundredMillion' > ("testParSimpleTwoHundredMillion" + [DateTimeOffset]::Now.ToUnixTimeSeconds() + ".txt")
mvn test '-Dtest=ReciprocalArraySumTest#testParManyTaskTwoMillion' > ("testParManyTaskTwoMillion" + [DateTimeOffset]::Now.ToUnixTimeSeconds() + ".txt")
mvn test '-Dtest=ReciprocalArraySumTest#testParManyTaskTwoHundredMillion' > ("testPartestParManyTaskTwoHundredMillionManyTaskTwoMillion" + [DateTimeOffset]::Now.ToUnixTimeSeconds() + ".txt")
mvn test > ("test" + [DateTimeOffset]::Now.ToUnixTimeSeconds() + ".txt")
```

```bash
mvn clean
mvn test -Dtest=ReciprocalArraySumTest#testParSimpleTwoMillion > testParSimpleTwoMillion$(date +%s).txt
mvn test -Dtest=ReciprocalArraySumTest#testParSimpleTwoHundredMillion > testParSimpleTwoHundredMillion$(date +%s).txt
mvn test -Dtest=ReciprocalArraySumTest#testParManyTaskTwoMillion > testParManyTaskTwoMillion$(date +%s).txt
mvn test -Dtest=ReciprocalArraySumTest#testParManyTaskTwoHundredMillion > testParManyTaskTwoHundredMillion$(date +%s).txt
mvn test > test$(date +%s).txt
```

```powershell
``` 