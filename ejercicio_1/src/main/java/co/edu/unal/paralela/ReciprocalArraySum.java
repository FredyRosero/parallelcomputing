package co.edu.unal.paralela;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Clase que contiene los métodos para implementar la suma de los recíprocos de un arreglo usando paralelismo.
 */
public final class ReciprocalArraySum {

    /**
     * Constructor.
     */
    private ReciprocalArraySum() {
    }

    /**
     * Calcula secuencialmente la suma de valores recíprocos para un arreglo.
     *
     * @param input Arreglo de entrada
     * @return La suma de los recíprocos del arreglo de entrada
     */
    protected static double seqArraySum(final double[] input) {
        double sum = 0;

        // Calcula la suma de los recíprocos de los elementos del arreglo
        for (int i = 0; i < input.length; i++) {
            sum += 1 / input[i];
        }

        return sum;
    }

    /**
     * calcula el tamaño de cada trozo o sección, de acuerdo con el número de secciones para crear
     * a través de un número dado de elementos.
     *
     * @param nChunks El número de secciones (chunks) para crear
     * @param nElements El número de elementos para dividir
     * @return El tamaño por defecto de la sección (chunk)
     */
    private static int getChunkSize(final int nChunks, final int nElements) {
        // Función techo entera
        return (nElements + nChunks - 1) / nChunks;
    }

    /**
     * Calcula el índice del elemento inclusivo donde la sección/trozo (chunk) inicia,
     * dado que hay cierto número de secciones/trozos (chunks).
     *
     * @param chunk la sección/trozo (chunk) para cacular la posición de inicio
     * @param nChunks Cantidad de secciones/trozos (chunks) creados
     * @param nElements La cantidad de elementos de la sección/trozo que deben atravesarse
     * @return El índice inclusivo donde esta sección/trozo (chunk) inicia en el conjunto de 
     *         nElements
     */
    private static int getChunkStartInclusive(final int chunk, final int nChunks, final int nElements) {
        final int chunkSize = getChunkSize(nChunks, nElements);
        return chunk * chunkSize;
    }

    /**
     * Calcula el índice del elemento exclusivo que es proporcionado al final de la sección/trozo (chunk),
     * dado que hay cierto número de secciones/trozos (chunks).
     *
     * @param chunk La sección para calcular donde termina
     * @param nChunks Cantidad de secciones/trozos (chunks) creados
     * @param nElements La cantidad de elementos de la sección/trozo que deben atravesarse
     * @return El índice de terminación exclusivo para esta sección/trozo (chunk)
     */
    private static int getChunkEndExclusive(final int chunk, final int nChunks,
            final int nElements) {
        final int chunkSize = getChunkSize(nChunks, nElements);
        final int end = (chunk + 1) * chunkSize;
        if (end > nElements) {
            return nElements;
        } else {
            return end;
        }
    }

    /**
     * Este pedazo de clase puede ser completada para para implementar el cuerpo de cada tarea creada
     * para realizar la suma de los recíprocos del arreglo en paralelo.
     */
    private static class ReciprocalArraySumTask extends RecursiveAction {
        /**
         * Iniciar el índice para el recorrido transversal hecho por esta tarea.
         */
        private final int startIndexInclusive;
        /**
         * Concluir el índice para el recorrido transversal hecho por esta tarea.
         */
        private final int endIndexExclusive;
        /**
         * Arreglo de entrada para la suma de recíprocos.
         */
        private final double[] input;
        /**
         * Valor intermedio producido por esta tarea.
         */
        private double value;

        /**
         * Constructor.
         * @param setStartIndexInclusive establece el índice inicial para comenzar
         *        el recorrido trasversal.
         * @param setEndIndexExclusive establece el índice final para el recorrido trasversal.
         * @param setInput Valores de entrada
         */
        ReciprocalArraySumTask(final int setStartIndexInclusive, final int setEndIndexExclusive, final double[] setInput) {
            this.startIndexInclusive = setStartIndexInclusive;
            this.endIndexExclusive = setEndIndexExclusive;
            this.input = setInput;
            System.out.println("NEW ReciprocalArraySumTask" + this.hashCode() + ":\t" + setStartIndexInclusive + "\t-\t" + setEndIndexExclusive);
        }

        /**
         * Adquiere el valor calculado por esta tarea.
         * @return El valor calculado por esta tarea
         */
        public double getValue() {
            return value;
        }

        /**
         * Implementación recursiva de la tarea para calcular la suma de los recíprocos del arreglo.
         * A la hora de computar la suma, se divide el trabajo en dos tareas más pequeñas.
         * LA Recursión Múltiple (Árbol Recursivo) se utiliza para dividir el trabajo en tareas más pequeñas.
         */
        @Override
        protected void compute() {
            if (endIndexExclusive - startIndexInclusive <= input.length/2) {
                System.out.println("ReciprocalArraySumTask" + this.hashCode() + " caso base.");
                double sum = 0;
                for (int i = startIndexInclusive; i < endIndexExclusive; i++) {
                    sum += 1 / input[i];
                }
                value = sum;
            } else {
                System.out.println("ReciprocalArraySumTask" + this.hashCode() + " recursion.");
                int mid = (startIndexInclusive + endIndexExclusive) / 2;
                ReciprocalArraySumTask left = new ReciprocalArraySumTask(startIndexInclusive, mid, input);
                ReciprocalArraySumTask right = new ReciprocalArraySumTask(mid, endIndexExclusive, input);
                /* 
                // Estrategia #1
                left.fork();
                right.compute();
                left.join();
                value = left.value + right.value;
                */
                /*
                // Estrategia #2
                ForkJoinTask.invokeAll(left, right);
                value = left.getValue() + right.getValue();
                */
                // Estrategia #3
                RecursiveAction.invokeAll(left, right);
                value = left.getValue() + right.getValue();
            }
        }
    }

    /**
     * Para hacer: Modificar este método para calcular la misma suma de recíprocos como le realizada en
     * seqArraySum, pero utilizando dos tareas ejecutándose en paralelo dentro del framework ForkJoin de Java
     * Se puede asumir que el largo del arreglo de entrada 
     * es igualmente divisible por 2.
     *
     * @param input Arreglo de entrada
     * @return La suma de los recíprocos del arreglo de entrada
     */
    protected static double parArraySum(final double[] input) {
        assert input.length % 2 == 0;

        double sum = 0;

        // Se crea la tarea para todo el arreglo.
        ReciprocalArraySumTask task = new ReciprocalArraySumTask(0, input.length, input);
        // Se utiliza el pool común para invocar la tarea, lo que permite que las sub-tareas se ejecuten en paralelo.
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        pool.invoke(task);
        sum = task.getValue();
        return sum;
    }

    /**
     * Para hacer: extender el trabajo hecho para implementar parArraySum que permita utilizar un número establecido
     * de tareas para calcular la suma del arreglo recíproco. 
     * getChunkStartInclusive y getChunkEndExclusive pueden ser útiles para cacular 
     * el rango de elementos índice que pertenecen a cada sección/trozo (chunk).
     *
     * @param input Arreglo de entrada
     * @param numTasks El número de tareas para crear
     * @return La suma de los recíprocos del arreglo de entrada
     */
    protected static double parManyTaskArraySum(final double[] input, final int numTasks) {
        ReciprocalArraySumTask[] tasks = new ReciprocalArraySumTask[numTasks];
        int nElements = input.length;
        // Se parte el arreglo en secciones/trozos (chunks) y se crea una tarea para cada sección/trozo (chunk).
        for (int i = 0; i < numTasks; i++) {
            // Para el i-ésimo trozo, se calcula el índice de inicio y fin.
            int start = getChunkStartInclusive(i, numTasks, nElements);
            int end = getChunkEndExclusive(i, numTasks, nElements);
            tasks[i] = new ReciprocalArraySumTask(start, end, input);
        }

        /* 
        // Estrategia #1
        // Ejecuta las tareas en paralelo usando fork/join
        for (int i = 0; i < numTasks; i++) {
            tasks[i].fork();
        }
        // Espera a que todas finalicen
        for (int i = 0; i < numTasks; i++) {
            tasks[i].join();
        }
        
        // Suma los resultados parciales de cada tarea
        double sum = 0;
        for (int i = 0; i < numTasks; i++) {
            sum += tasks[i].getValue();
        }
        return sum;
        */

        /*
        // Estrategia #2
        // Invoca todas las tareas en paralelo de forma optimizada.
        ForkJoinTask.invokeAll(tasks);
        */

        // Estrategia #3
        // 
        RecursiveAction.invokeAll(tasks);
        
        // Suma los resultados parciales.
        double sum = 0;
        for (ReciprocalArraySumTask task : tasks) {
            sum += task.getValue();
        }
        return sum;
    }
}
