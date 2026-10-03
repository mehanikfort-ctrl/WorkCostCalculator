package com.example.workcost

import android.content.Context

data class WorkItem(
    val id: Int,
    val category: String,
    val name: String,
    val unit: String,
    var price: Double,
    val isCustom: Boolean = false,
    val customId: Long = 0L
)

object WorkRepository {
    val defaultCategories = listOf("Строительные", "Земляные", "Электрика", "Кровля")

    val defaultWorks = listOf(
        WorkItem(1, "Строительные", "Кладка газобетонных блоков (на клей)", "м2", 360.0),
        WorkItem(2, "Строительные", "Кладка перегородок из ПГП", "м2", 450.0),
        WorkItem(3, "Строительные", "Кладка облицовочного кирпича", "м2", 1500.0),
        WorkItem(4, "Строительные", "Штукатурка стен (механизированная)", "м2", 550.0),
        WorkItem(5, "Строительные", "Стяжка пола (полусухая)", "м2", 380.0),
        WorkItem(6, "Строительные", "Монтаж опалубки", "м2", 400.0),
        WorkItem(7, "Строительные", "Армирование фундамента", "т", 25000.0),
        WorkItem(8, "Строительные", "Бетонирование фундамента", "м3", 3500.0),
        WorkItem(9, "Земляные", "Ручная разработка грунта", "м3", 370.0),
        WorkItem(10, "Земляные", "Разработка грунта экскаватором", "м3", 430.0),
        WorkItem(11, "Земляные", "Механизированные земляные работы", "м3", 290.0),
        WorkItem(12, "Земляные", "Копка колодцев (до 2 м)", "м3", 2500.0),
        WorkItem(13, "Земляные", "Засыпка грунта", "м3", 520.0),
        WorkItem(14, "Земляные", "Устройство песчаной подушки", "м3", 800.0),
        WorkItem(15, "Электрика", "Монтаж электропроводки (в штробу)", "точка", 200.0),
        WorkItem(16, "Электрика", "Установка розетки", "шт", 350.0),
        WorkItem(17, "Электрика", "Установка выключателя", "шт", 300.0),
        WorkItem(18, "Электрика", "Монтаж электрощита", "шт", 5000.0),
        WorkItem(19, "Электрика", "Подключение к центральным сетям", "п.м.", 32289.0),
        WorkItem(20, "Кровля", "Плоская наплавляемая кровля (1 слой)", "м2", 90.0),
        WorkItem(21, "Кровля", "Плоская наплавляемая кровля (2 слоя)", "м2", 160.0),
        WorkItem(22, "Кровля", "Кровля из битумной черепицы", "м2", 650.0),
        WorkItem(23, "Кровля", "Кровля из керамической черепицы", "м2", 1030.0),
        WorkItem(24, "Кровля", "Монтаж утепленной кровли из металлочерепицы", "м2", 1200.0),
        WorkItem(25, "Кровля", "Монтаж водосточной системы", "п.м.", 450.0)
    )

    fun getAllCategories(context: Context): List<String> {
        val custom = CustomWorkStorage.loadCategories(context).map { it.name }
        return defaultCategories + custom
    }

    fun getWorks(context: Context): List<WorkItem> {
        val savedPrices = PriceStorage.loadPrices(context)
        val standard = defaultWorks.map { work ->
            work.copy(price = savedPrices[work.id] ?: work.price)
        }
        val custom = CustomWorkStorage.loadWorks(context).map { customWork ->
            WorkItem(
                id = customWork.id.toInt(),
                category = customWork.category,
                name = customWork.name,
                unit = customWork.unit,
                price = customWork.price,
                isCustom = true,
                customId = customWork.id
            )
        }
        return standard + custom
    }

    val allWorks: List<WorkItem> get() = defaultWorks
}
