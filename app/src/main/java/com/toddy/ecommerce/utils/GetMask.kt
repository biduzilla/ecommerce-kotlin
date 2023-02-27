package com.toddy.ecommerce.utils

import java.text.*
import java.util.*

class GetMask {
    companion object {
        fun getDate(dataPublicacao: Long, tipo: Int): String {
            val DIA_MES_ANO: Int = 1 // 31/12/2021
            val HORA_MINUTO: Int = 2 // 22:00
            val DIA_MES_ANO_HORA_MINUTO: Int = 3 // 31/12/2022 as 22:00
            val DIA_MES: Int = 4 // 31 Janeiro

            val locale: Locale = Locale("PT", "br")
            val fuso: String = "America/Sao_Paulo"

            val diaSDF: SimpleDateFormat = SimpleDateFormat("dd", locale)
            diaSDF.timeZone = TimeZone.getTimeZone(fuso)


            val mesSDF: SimpleDateFormat = SimpleDateFormat("MM", locale)
            mesSDF.timeZone = TimeZone.getTimeZone(fuso)

            val anoSDF: SimpleDateFormat = SimpleDateFormat("yyyy", locale)
            anoSDF.timeZone = TimeZone.getTimeZone(fuso)

            val horaSDF: SimpleDateFormat = SimpleDateFormat("HH", locale)
            horaSDF.timeZone = TimeZone.getTimeZone(fuso)

            val minutoSDF: SimpleDateFormat = SimpleDateFormat("mm", locale)
            minutoSDF.timeZone = TimeZone.getTimeZone(fuso)

            val dateFormat: DateFormat = DateFormat.getDateInstance()
            val netDate: Date = Date(dataPublicacao)
            dateFormat.format(netDate)

            val hora: String = horaSDF.format(netDate)
            val minuto: String = minutoSDF.format(netDate)
            val dia: String = diaSDF.format(netDate)
            var mes: String = mesSDF.format(netDate)
            val ano: String = anoSDF.format(netDate)

            var time: String
            when (tipo) {
                DIA_MES -> {
                    when (mes) {
                        "01" -> mes = "Janeiro"
                        "02" -> mes = "Fevereiro"
                        "03" -> mes = "Março"
                        "04" -> mes = "Abril"
                        "05" -> mes = "Maio"
                        "06" -> mes = "Junho"
                        "07" -> mes = "Julho"
                        "08" -> mes = "Agosto"
                        "09" -> mes = "Setembro"
                        "10" -> mes = "Outobro"
                        "11" -> mes = "Novembro"
                        "12" -> mes = "Dezembro"
                        else -> time = "Error"
                    }
                    time = "${dia} ${mes}"
                }
                DIA_MES_ANO -> time = "${dia}/${mes}/${ano}"
                HORA_MINUTO -> time = "${hora}:${minuto}"
                DIA_MES_ANO_HORA_MINUTO -> time = "${dia}/${mes}/${ano}  ${hora}:${minuto}"
                else -> time = "Error"
            }
            return time;
        }

        fun getValor(valor: Double): String {
            val nf: NumberFormat =
                DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("Pt", "br")))
            return nf.format(valor)
        }
    }
}