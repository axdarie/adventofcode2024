package days.day02

import common.readFile
import java.lang.Math.abs

private const val FILENAME =
  "day02/input.txt"

fun readValues(): List<List<Int>> =
  readFile(FILENAME).map { line -> line.split(" ").map { it.toInt() } }

enum class Direction {
  Up, Down, Flat
}

fun run() {
  val reports = readValues()
  val count = reports.count { safeReport(it, true) }
  println(count)
}

fun safeReport(report: List<Int>, tryAlternatives: Boolean = false): Boolean {
  val initialDirection = direction(report[1] - report[0])

  if (initialDirection == Direction.Flat) {
    return if (tryAlternatives) trySafeReportVariants(report) else false
  }

  for (i in 1 until report.size) {
    val isSafe = safeLevel(
      previousDirection = initialDirection,
      current = report[i],
      before = report[i - 1],
      after = report.getOrNull(i + 1)
    )
    if (!isSafe) {
      return if (tryAlternatives) trySafeReportVariants(report, i) else false
    }
  }
  return true
}

fun trySafeReportVariants(report: List<Int>, index: Int? = null): Boolean {
  val indicesToRemove = when (index) {
    null -> listOf(0, 1, 2)
    else -> listOf(index, index + 1, index - 1)
  }

  return indicesToRemove.any { idx ->
    idx in report.indices && safeReport(report.filterIndexed { i, _ -> i != idx })
  }
}

fun safeLevel(previousDirection: Direction, current: Int, before: Int, after: Int?): Boolean {
  val deltaLeft = current - before
  val deltaRight = after?.let { it - current } ?: deltaLeft

  val isConsistentDirection = direction(deltaLeft) == direction(deltaRight)
  val withinBounds = abs(deltaLeft) <= 3 && abs(deltaRight) <= 3
  val matchesPreviousDirection = direction(deltaLeft) == previousDirection

  return isConsistentDirection && withinBounds && matchesPreviousDirection
}

fun direction(diff: Int): Direction = when {
  diff == 0 -> Direction.Flat
  diff > 0 -> Direction.Up
  else -> Direction.Down
}
