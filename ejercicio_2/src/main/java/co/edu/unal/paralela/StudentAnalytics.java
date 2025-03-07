package co.edu.unal.paralela;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Una clase 'envoltorio' (wrapper) para varios métodos analíticos.
 */
public final class StudentAnalytics {
    /**
     * Calcula secuencialmente la edad promedio de todos los estudientes registrados y activos 
     * utilizando ciclos.
     *
     * @param studentArray Datos del estudiante para la clase.
     * @return Edad promedio de los estudiantes registrados
     */
    public double averageAgeOfEnrolledStudentsImperative(
            final Student[] studentArray) {
        List<Student> activeStudents = new ArrayList<Student>();

        for (Student s : studentArray) {
            if (s.checkIsCurrent()) {
                activeStudents.add(s);
            }
        }

        double ageSum = 0.0;
        for (Student s : activeStudents) {
            ageSum += s.getAge();
        }

        return ageSum / (double) activeStudents.size();
    }

    /**
     * PARA HACER calcular la edad promedio de todos los estudiantes registrados y activos usando
     * streams paralelos. Debe reflejar la funcionalidad de 
     * averageAgeOfEnrolledStudentsImperative. Este método NO debe utilizar ciclos.
     *
     * @param studentArray Datos del estudiante para esta clase.
     * @return Edad promedio de los estudiantes registrados
     */
    public double averageAgeOfEnrolledStudentsParallelStream(
            final Student[] studentArray) {
        /*        
        // Sequential stream
        return Stream.of(studentArray)
                .filter(s -> s.checkIsCurrent())
                .mapToDouble(Student::getAge)
                .average()
                .getAsDouble();
        */
        // Se crea un Stream a partir del arreglo y se convierte a un stream paralelo.
        // Aquí, 'parallel()' actúa como un funtor: transforma el stream secuencial en un stream paralelo
        // sin alterar la estructura subyacente (la colección de estudiantes).
        return Stream.of(studentArray)
                // paralelismo funcional:
                .parallel() // Funtor de paralelismo: preserva la estructura del stream mientras habilita procesamiento en paralelo.
                // 'filter' es un endomorfismo en la categoría de Streams: 
                // toma un Stream<Student> y devuelve otro Stream<Student> que contiene solo los elementos que cumplen la condición.
                .filter(s -> s.checkIsCurrent()) // Stream<Student> -> Stream<Student>
                // 'mapToDouble' aplica un morfismo entre categorías:
                // transforma cada Student en un valor double (su edad), pasando de un Stream<Student> a un DoubleStream.
                .mapToDouble(Student::getAge) // Stream<Student> -> DoubleStream
                // 'average' es una operación reductora (catamorfismo) que reduce la estructura de un DoubleStream a un OptionalDouble.
                // Aquí, OptionalDouble encapsula el resultado (o la ausencia de él) en un contexto monádico.
                .average() // DoubleStream -> OptionalDouble
                // Extrae el valor primitivo double de la mónada OptionalDouble; se asume que siempre hay un valor.
                .getAsDouble(); // OptionalDouble -> double

        // OptionalDouble se considera una mónada porque provee operaciones de 'map' y 'flatMap'
        // (a través de métodos similares en su API) y cumple con las leyes monádicas, permitiendo
        // el encadenamiento seguro de operaciones en un contexto de posible ausencia de valor.

        // Un catamorfismo es un concepto proveniente de la teoría de categorías que se utiliza en programación
        // funcional para describir el proceso de "plegar" o descomponer estructuras de datos recursivas en un único valor.
    }

    /**
     * Calcula secuencialmente -usando ciclos- el nombre más común de todos los estudiantes 
     * que no están activos en la clase.
     *
     * @param studentArray Datos del estudiante para esta clase.
     * @return Nombre más común de los estudiantes inactivos.
     */
    public String mostCommonFirstNameOfInactiveStudentsImperative(
            final Student[] studentArray) {
        List<Student> inactiveStudents = new ArrayList<Student>();

        for (Student s : studentArray) {
            if (!s.checkIsCurrent()) {
                inactiveStudents.add(s);
            }
        }

        Map<String, Integer> nameCounts = new HashMap<String, Integer>();

        for (Student s : inactiveStudents) {
            if (nameCounts.containsKey(s.getFirstName())) {
                nameCounts.put(s.getFirstName(),
                        new Integer(nameCounts.get(s.getFirstName()) + 1));
            } else {
                nameCounts.put(s.getFirstName(), 1);
            }
        }

        String mostCommon = null;
        int mostCommonCount = -1;
        for (Map.Entry<String, Integer> entry : nameCounts.entrySet()) {
            if (mostCommon == null || entry.getValue() > mostCommonCount) {
                mostCommon = entry.getKey();
                mostCommonCount = entry.getValue();
            }
        }

        return mostCommon;
    }

    /**
     * PARA HACER calcula el nombre más común de todos los estudiantes que no están activos
     * en la clase utilizando streams paralelos. Debe reflejar la funcionalidad 
     * de mostCommonFirstNameOfInactiveStudentsImperative. Este método NO debe usar ciclos
     *
     * @param studentArray Datos de estudiantes para la clase.
     * @return Nombre más comun de los estudiantes inactivos.
     */
    public String mostCommonFirstNameOfInactiveStudentsParallelStream(
        final Student[] studentArray) {
    // Se crea un Stream a partir del arreglo de estudiantes y se lo convierte en un stream paralelo.
    // Esto aplica un funtor de paralelismo que preserva la estructura del stream mientras habilita el procesamiento concurrente.
    return Stream.of(studentArray)
            // Se convierte el stream en un stream paralelo, un funtor para paralelismo
            .parallel() // funtor:Stream<Student> -> funtor:Stream(Parelelismo)<Student>
            // Se filtran los estudiantes para conservar solo aquellos que NO están activos.
            // Este 'filter' actúa como un endomorfismo en la categoría de Streams, transformando un Stream<Student> en otro de la misma forma.
            .filter(s -> !s.checkIsCurrent()) // funtor:Stream(Parelelismo)<Student> -> funtor:Stream(Parelelismo)<Student>
            // Se aplica una operación de reducción 'groupingBy' que agrupa los elementos del stream en un Map.
            // El funtor 'groupingBy' mapea cada estudiante a su primer nombre (Student::getFirstName),
            // agrupando aquellos con la misma clave, lo que corresponde a un catamorfismo que reduce la estructura a un Map<String, List<Student>>.
            // Sin embargo, al usar el downstream 'counting()', se transforma cada grupo en un conteo (Long),
            // obteniéndose así un Map<String, Long> que representa la frecuencia de cada nombre.
            .collect // operación terminal que convierte el funtor stream(Parelelismo) en un valor final objeto
            (
                    java.util.stream.Collectors.groupingBy // función de orden superior 
                    ( 
                        Student::getFirstName, // morfismo: Student -> String
                        java.util.stream.Collectors.counting() // catamorfismo: Stream<Student> -> Long (downstream collector)
                    ) 
            ) // Stream<Student> -> Map<String, Long>
            // Se transforma el Map en un stream de entradas (Entry<String, Long>) para poder aplicar una operación reductora 'max'.
            .entrySet() // objeto:Map<String, Long> -> objeto:Set<Map.Entry<String, Long>>
            .stream() // objeto:Set<Map.Entry<String, Long>> -> funtor:Stream<Map.Entry<String, Long>>
            // 'max' es un catamorfismo que, mediante un comparador, reduce el stream a un único elemento: 
            // el par clave-valor con el máximo valor (la mayor cantidad de estudiantes con el mismo nombre).
            .max(java.util.Map.Entry.comparingByValue()) // Stream<Map.Entry<String, Long>> -> Optional<Map.Entry<String, Long>>
            // 'get' extrae el resultado del Optional generado por 'max'.
            .get() // Optional<Map.Entry<String, Long>> -> Map.Entry<String, Long>
            // 'getKey' extrae la clave (el primer nombre) del par con el máximo conteo.
            .getKey(); // Map.Entry<String, Long> -> String    
    }


    /**
     * calcula secuencialmente el número de estudiantes que han perdido el curso 
     * que son mayores de 20 años. Una calificación de perdido es cualquiera por debajo de 65 
     * 65. Un estudiante ha perdido el curso si tiene una calificación de perdido 
     * y no está activo en la actuialidad
     *
     * @param studentArray Datos del estudiante para la clase.
     * @return Cantidad de calificacione sperdidas de estudiantes mayores de 20 años de edad.
     */
    public int countNumberOfFailedStudentsOlderThan20Imperative(
            final Student[] studentArray) {
        int count = 0;
        for (Student s : studentArray) {
            if (!s.checkIsCurrent() && s.getAge() > 20 && s.getGrade() < 65) {
                count++;
            }
        }
        return count;
    }

    /**
     * PARA HACER calcular el número de estudiantes que han perdido el curso 
     * que son mayores de 20 años de edad . una calificación de perdido está por debajo de 65. 
     * Un estudiante ha perdido el curso si tiene una calificación de perdido 
     * y no está activo en la actuialidad. Debe reflejar la funcionalidad de 
     * countNumberOfFailedStudentsOlderThan20Imperative. El método no debe usar ciclos.
	 *
     * @param studentArray Datos del estudiante para la clase.
     * @return Cantidad de calificacione sperdidas de estudiantes mayores de 20 años de edad.
     */
    public int countNumberOfFailedStudentsOlderThan20ParallelStream(
            final Student[] studentArray) {
        return (int) Stream.of(studentArray)
                .parallel() // funtor:Stream<Student> -> funtor:Stream(Parelelismo)<Student>
                .filter(s -> !s.checkIsCurrent() && s.getAge() > 20 && s.getGrade() < 65) // morfismo: funtor:Stream(Parelelismo)<Student> -> funtor:Stream(Parelelismo)<Student>
                .count(); // catamorfismo: funtor:Stream(Parelelismo)<Student> -> long
    }
}
