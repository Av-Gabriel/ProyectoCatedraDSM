import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// -------------------------------------------------------------
// PALETA PROPIA — independiente de los colores default de Material3
// -------------------------------------------------------------
// Concepto: Tonos tinta/carbón sofisticados para la estructura,
// combinados con una paleta financiera suave (esmeralda, pizarra y coral)
// que es altamente legible tanto en entornos claros como oscuros.

data class AppColorScheme(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val onSurfaceMuted: Color,
    val primary: Color,
    val onPrimary: Color,
    val topBar: Color,
    val onTopBar: Color,
    val bottomBar: Color,
    val iconActivo: Color,
    val iconInactivo: Color,
    val indicadorActivo: Color,
    val acentoAgregar: Color,
    val onAcentoAgregar: Color,
    val divider: Color,
    // --- Extensiones específicas para Finanzas ---
    val ingreso: Color,          // Balances positivos, depósitos
    val egreso: Color,           // Gastos, alertas, balances negativos
    val ahorroInversion: Color   // Cuentas de ahorro, fondos, categorías secundarias
)

val LightAppColors = AppColorScheme(
    background = Color(0xFFF8F9FA),      // Fondo claro limpio y suave
    surface = Color(0xFFFFFFFF),         // Tarjetas y contenedores principales
    surfaceVariant = Color(0xFFEDEDEA),  // Separadores de listas o tarjetas secundarias
    onSurface = Color(0xFF20232B),       // Tinta oscuro para máxima legibilidad
    onSurfaceMuted = Color(0xFF7A7F8C),  // Gris neutro para subtítulos y fechas
    primary = Color(0xFF2E7D63),         // Esmeralda corporativo suave
    onPrimary = Color(0xFFFFFFFF),
    topBar = Color(0xFFFFFFFF),
    onTopBar = Color(0xFF20232B),
    bottomBar = Color(0xFFFFFFFF),
    iconActivo = Color(0xFF1E293B),      // Iconos alineados a la identidad esmeralda
    iconInactivo = Color(0xFFB0B3BD),
    indicadorActivo = Color(0xFF2E7D63),
    acentoAgregar = Color(0xFF2E7D63),   // El botón flotante destaca de forma elegante
    onAcentoAgregar = Color(0xFFFFFFFF),
    divider = Color(0xFFE7E7E4),
    // Finanzas
    ingreso = Color(0xFF2E7D63),         // Verde financiero suave
    egreso = Color(0xFFD32F2F),          // Coral/Rojo sutil para gastos
    ahorroInversion = Color(0xFF4A6572)  // Azul pizarra para balances neutrales
)

val DarkAppColors = AppColorScheme(
    background = Color(0xFF121414),      // Negro mate OLED-friendly (reduce fatiga)
    surface = Color(0xFF1A1D1E),         // Gris contenedor para elevar componentes
    surfaceVariant = Color(0xFF232729),  // Sub-tarjetas
    onSurface = Color(0xFFF0F0EE),       // Blanco suave roto para evitar destellos
    onSurfaceMuted = Color(0xFF9498A3),  // Gris atenuado para textos secundarios
    primary = Color(0xFF66C2A5),         // Esmeralda pastel altamente legible en oscuro
    onPrimary = Color(0xFF003827),
    topBar = Color(0xFF1A1D1E),
    onTopBar = Color(0xFFF0F0EE),
    bottomBar = Color(0xFF1A1D1E),
    iconActivo = Color(0xFF66C2A5),
    iconInactivo = Color(0xFF5B5F6A),
    indicadorActivo = Color(0xFF66C2A5),
    acentoAgregar = Color(0xFF66C2A5),   // Resalta en la oscuridad de forma suave
    onAcentoAgregar = Color(0xFF003827),
    divider = Color(0xFF2C2F37),
    // Finanzas
    ingreso = Color(0xFF66C2A5),         // Verde pastel financiero
    egreso = Color(0xFFE57373),          // Coral pastel para alertas de gastos
    ahorroInversion = Color(0xFF90A4AE)  // Azul pizarra claro
)

private val LocalAppColors = staticCompositionLocalOf { LightAppColors }

/**
 * Uso: AppTheme.colors.ingreso  (en vez de MaterialTheme.colorScheme.primary)
 */
object AppTheme {
    val colors: AppColorScheme
        @Composable
        get() = LocalAppColors.current
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colores = if (darkTheme) DarkAppColors else LightAppColors
    CompositionLocalProvider(LocalAppColors provides colores) {
        // Vinculamos de forma segura las propiedades críticas de MaterialTheme
        // para que componentes nativos (como AlertDialogs, Ripples o menús)
        // adopten tu paleta personalizada automáticamente de fondo.
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = colores.primary,
                background = colores.background,
                surface = colores.surface,
                error = colores.egreso
            ),
            content = content
        )
    }
}
