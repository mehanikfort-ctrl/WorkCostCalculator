package com.example.workcost

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f

    fun exportEstimate(context: Context, estimate: Estimate): File {
        val document = PdfDocument()

        // Стили текста
        val titlePaint = Paint().apply {
            color = Color.rgb(230, 81, 0)
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val subtitlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            isAntiAlias = true
        }
        val headerPaint = Paint().apply {
            color = Color.WHITE
            textSize = 11f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val textPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 10f
            isAntiAlias = true
        }
        val totalPaint = Paint().apply {
            color = Color.rgb(230, 81, 0)
            textSize = 14f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 0.5f
        }
        val headerBgPaint = Paint().apply {
            color = Color.rgb(230, 81, 0)
        }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        var y = MARGIN + 60f

        // === ЛОГОТИП В ПРАВОМ ВЕРХНЕМ УГЛУ ===
        try {
            val logoId = context.resources.getIdentifier("logo", "drawable", context.packageName)
            if (logoId != 0) {
                val bitmap = BitmapFactory.decodeResource(context.resources, logoId)
                if (bitmap != null) {
                    val logoSize = 80
                    val logoX = PAGE_WIDTH - MARGIN - logoSize
                    val logoY = MARGIN.toInt()
                    val scaledLogo = Bitmap.createScaledBitmap(bitmap, logoSize, logoSize, true)
                    canvas.drawBitmap(scaledLogo, logoX.toFloat(), logoY.toFloat(), null)
                }
            }
        } catch (e: Exception) {
            // Если логотип не найден — просто пропускаем
        }

        // Заголовок
        canvas.drawText("СМЕТА НА ВЫПОЛНЕНИЕ РАБОТ", MARGIN, y, titlePaint)
        y += 30f

        // Данные объекта
        canvas.drawText("Объект: ${estimate.name}", MARGIN, y, subtitlePaint)
        y += 18f
        canvas.drawText("Заказчик: ${estimate.customer}", MARGIN, y, subtitlePaint)
        y += 18f
        canvas.drawText("Адрес: ${estimate.address}", MARGIN, y, subtitlePaint)
        y += 18f
        canvas.drawText("Дата: ${estimate.date}", MARGIN, y, subtitlePaint)
        y += 25f

        // Шапка таблицы
        val colNum = MARGIN
        val colName = MARGIN + 30f
        val colQty = MARGIN + 290f
        val colPrice = MARGIN + 380f
        val colSum = MARGIN + 470f

        canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 20f, headerBgPaint)
        canvas.drawText("№", colNum, y + 14f, headerPaint)
        canvas.drawText("Наименование", colName, y + 14f, headerPaint)
        canvas.drawText("Объём", colQty, y + 14f, headerPaint)
        canvas.drawText("Цена", colPrice, y + 14f, headerPaint)
        canvas.drawText("Сумма", colSum, y + 14f, headerPaint)
        y += 26f

        // Строки таблицы
        estimate.items.forEachIndexed { index, item ->
            if (y > PAGE_HEIGHT - MARGIN - 80f) {
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = MARGIN + 20f
            }

            canvas.drawText("${index + 1}", colNum, y, textPaint)

            val nameText = item.name
            if (nameText.length > 45) {
                canvas.drawText(nameText.substring(0, 45), colName, y, textPaint)
                y += 14f
                canvas.drawText(nameText.substring(45), colName, y, textPaint)
            } else {
                canvas.drawText(nameText, colName, y, textPaint)
            }

            canvas.drawText("${item.quantity} ${item.unit}", colQty, y, textPaint)
            canvas.drawText("${item.price} ₽", colPrice, y, textPaint)
            canvas.drawText("${"%.2f".format(item.sum())} ₽", colSum, y, textPaint)
            y += 20f
        }

        // Итого
        y += 10f
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 25f
        canvas.drawText("ИТОГО: ${"%.2f".format(estimate.totalSum())} ₽", colPrice - 40f, y, totalPaint)

        document.finishPage(page)

        // Сохраняем файл
        val dateFormat = SimpleDateFormat("dd.MM.yyyy_HH-mm", Locale.getDefault())
        val fileName = "smeta_${estimate.name.replace(" ", "_")}_${dateFormat.format(Date())}.pdf"
        val file = File(context.cacheDir, fileName)
        document.writeTo(file.outputStream())
        document.close()

        return file
    }

    fun sharePdf(context: Context, estimate: Estimate) {
        val file = exportEstimate(context, estimate)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Смета: ${estimate.name}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Отправить смету"))
    }
}
