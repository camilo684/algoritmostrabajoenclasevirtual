import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

/**
 * Genera datos inventados y mide el tiempo de cada algoritmo de
 * {@link Ordenamientos}.
 *
 * Uso: java PruebasRendimiento 5000
 */
public class PruebasRendimiento {
    private static final String[] PALABRAS = {
        "Reunion", "Entrega", "Pago", "Revision", "Soporte", "Llamada",
        "Taller", "Parcial", "Mantenimiento", "Despliegue", "Informe", "Auditoria"
    };

    /** Crea {@code cantidad} eventos aleatorios (id, fecha, descripcion y prioridad). */
    public static Evento[] generarDatos(int cantidad, long semilla) {
        if (cantidad < 0) throw new IllegalArgumentException("cantidad >= 0");
        Random rnd = new Random(semilla);
        long inicio = Instant.parse("2024-01-01T00:00:00Z").getEpochSecond();
        long fin = Instant.parse("2026-12-31T23:59:59Z").getEpochSecond();
        Evento[] datos = new Evento[cantidad];
        for (int i = 0; i < cantidad; i++) {
            int id = rnd.nextInt(1_000_000);
            Instant fecha = Instant.ofEpochSecond(inicio + (long) (rnd.nextDouble() * (fin - inicio)));
            String desc = PALABRAS[rnd.nextInt(PALABRAS.length)] + " #" + i;
            int prioridad = rnd.nextInt(10);
            datos[i] = new Evento(id, fecha, desc, prioridad);
        }
        return datos;
    }

    /**
     * Genera {@code cantidad} datos inventados y mide cuanto tarda cada algoritmo.
     * Todos los algoritmos ordenan una copia de los MISMOS datos para que la
     * comparacion sea justa. Devuelve un mapa algoritmo -> milisegundos.
     */
    public static Map<String, Double> medirTiempos(int cantidad) {
        Evento[] base = generarDatos(cantidad, 2026);
        Comparator<Evento> c = Ordenamientos.POR_PRIORIDAD;
        Comparator<Evento> porId = Comparator.comparingInt(Evento::getId);

        Map<String, Double> tiempos = new LinkedHashMap<>();
        medir(tiempos, "Bubble",    base, a -> Ordenamientos.bubble(a, c), c);
        medir(tiempos, "Selection", base, a -> Ordenamientos.selection(a, c), c);
        medir(tiempos, "Insertion", base, a -> Ordenamientos.insertion(a, c), c);
        medir(tiempos, "Merge",     base, a -> Ordenamientos.merge(a, c), c);
        medir(tiempos, "Quick",     base, a -> Ordenamientos.quick(a, c), c);
        medir(tiempos, "Heap",      base, a -> Ordenamientos.heap(a, c), c);
        medir(tiempos, "Counting",  base, Ordenamientos::counting, c);
        medir(tiempos, "Radix",     base, Ordenamientos::radix, porId);
        medir(tiempos, "Bucket",    base, Ordenamientos::bucket, c);
        medir(tiempos, "Shell",     base, a -> Ordenamientos.shell(a, c), c);

        imprimirTabla(cantidad, tiempos);
        return tiempos;
    }

    private static void medir(Map<String, Double> tiempos, String nombre, Evento[] base,
                              Consumer<Evento[]> ordenar, Comparator<Evento> criterio) {
        ordenar.accept(base.clone());                 // calentamiento de la JVM
        Evento[] a = base.clone();
        long t0 = System.nanoTime();
        ordenar.accept(a);
        long t1 = System.nanoTime();
        for (int i = 1; i < a.length; i++)
            if (criterio.compare(a[i - 1], a[i]) > 0)
                throw new AssertionError(nombre + " no ordeno correctamente");
        tiempos.put(nombre, (t1 - t0) / 1_000_000.0);
    }

    private static void imprimirTabla(int cantidad, Map<String, Double> tiempos) {
        System.out.println("Pruebas de rendimiento con " + cantidad + " datos inventados");
        System.out.println("+------------+---------------+");
        System.out.println("| Algoritmo  | Tiempo (ms)   |");
        System.out.println("+------------+---------------+");
        tiempos.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .forEach(e -> System.out.printf("| %-10s | %13.3f |%n", e.getKey(), e.getValue()));
        System.out.println("+------------+---------------+");
    }

    public static void main(String[] args) {
        int cantidad = args.length > 0 ? Integer.parseInt(args[0]) : 5000;
        medirTiempos(cantidad);
    }
}
