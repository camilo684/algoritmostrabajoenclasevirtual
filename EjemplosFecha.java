import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/**
 * Tres ejemplos de ordenamiento por fecha de creacion usando
 * los algoritmos que ya existen en {@link Ordenamientos}.
 */
public class EjemplosFecha {
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.of("UTC"));

    static Evento ev(int id, String fecha, String desc, int prioridad) {
        return new Evento(id, Instant.parse(fecha), desc, prioridad);
    }

    static void imprimir(String titulo, Evento[] a) {
        System.out.println(titulo);
        for (Evento e : a)
            System.out.printf("   %s | id=%-3d p=%d | %s%n",
                FMT.format(e.getFechaCreacion()), e.getId(),
                e.getPrioridad(), e.getDescripcion());
    }

    static void verificar(Evento[] a, Comparator<Evento> c) {
        for (int i = 1; i < a.length; i++)
            if (c.compare(a[i - 1], a[i]) > 0)
                throw new AssertionError("No quedo ordenado por fecha");
    }

    /** Ejemplo 1: Merge sort por fecha, de la mas antigua a la mas reciente. */
    static void ejemploMergePorFecha() {
        Evento[] a = {
            ev(1, "2026-03-15T10:30:00Z", "Entrega de taller", 2),
            ev(2, "2026-01-02T08:00:00Z", "Inicio de clases", 1),
            ev(3, "2026-05-20T14:00:00Z", "Parcial final", 0),
            ev(4, "2026-02-10T09:15:00Z", "Quiz de Java", 3),
            ev(5, "2026-04-01T16:45:00Z", "Exposicion grupal", 2),
        };
        imprimir("Ejemplo 1 - Merge por fecha (antes):", a);
        Ordenamientos.merge(a, Ordenamientos.POR_FECHA);
        verificar(a, Ordenamientos.POR_FECHA);
        imprimir("Ejemplo 1 - Merge por fecha (despues, ascendente):", a);
        System.out.println();
    }

    /** Ejemplo 2: Quick sort por fecha descendente (la mas reciente primero). */
    static void ejemploQuickPorFechaDescendente() {
        Evento[] a = {
            ev(10, "2025-12-24T20:00:00Z", "Cena de navidad", 4),
            ev(11, "2026-07-04T12:00:00Z", "Vacaciones", 6),
            ev(12, "2025-10-31T18:00:00Z", "Fiesta de disfraces", 7),
            ev(13, "2026-01-01T00:00:00Z", "Ano nuevo", 5),
            ev(14, "2026-02-14T19:30:00Z", "San Valentin", 3),
            ev(15, "2025-11-15T07:00:00Z", "Maraton", 2),
        };
        Comparator<Evento> descendente = Ordenamientos.POR_FECHA.reversed();
        imprimir("Ejemplo 2 - Quick por fecha (antes):", a);
        Ordenamientos.quick(a, descendente);
        verificar(a, descendente);
        imprimir("Ejemplo 2 - Quick por fecha (despues, descendente):", a);
        System.out.println();
    }

    /**
     * Ejemplo 3: Insertion sort (estable) por fecha. Hay eventos con la misma
     * fecha y se observa que conservan su orden original (estabilidad).
     */
    static void ejemploInsertionPorFechaEstable() {
        Evento[] a = {
            ev(20, "2026-06-01T09:00:00Z", "Reunion A (primera)", 1),
            ev(21, "2026-05-01T09:00:00Z", "Soporte", 5),
            ev(22, "2026-06-01T09:00:00Z", "Reunion B (segunda)", 0),
            ev(23, "2026-04-15T11:00:00Z", "Revision de codigo", 3),
            ev(24, "2026-06-01T09:00:00Z", "Reunion C (tercera)", 2),
        };
        imprimir("Ejemplo 3 - Insertion por fecha (antes):", a);
        Ordenamientos.insertion(a, Ordenamientos.POR_FECHA);
        verificar(a, Ordenamientos.POR_FECHA);
        imprimir("Ejemplo 3 - Insertion por fecha (despues, A-B-C conservan su orden):", a);
        System.out.println();
    }

    public static void main(String[] args) {
        ejemploMergePorFecha();
        ejemploQuickPorFechaDescendente();
        ejemploInsertionPorFechaEstable();
    }
}
