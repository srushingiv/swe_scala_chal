package org.srushingiv.swe

import java.io.File

object Main extends App {
  if (args.length != 1) println("usage: run <filename>")
  else {
    val filename = args(0)
    val file = new File(filename)
    if (!file.exists() || !file.isFile()) println("missing file: "+ filename)
    else {
      val occurrences = WordCounter.countWordsOccurrencesIn(file)
      occurrences foreach { case (word, count) =>
        println(s"$word $count")
      }
    }
  }
}
