package kiss.gossr.spring

import kiss.gossr.GossRenderer
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class LocalDateRequestParamConverter : Converter<String, LocalDate?> {
    override fun convert(source: String): LocalDate? {
        val source = source.trim()
        return if(source.isBlank()) null else LocalDate.parse(source, GossRenderer.dateFormatISO)
    }
}
