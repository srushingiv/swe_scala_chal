package org.srushingiv.swe

import java.io.File
import scala.io.{Source, Codec}

object WordCounter {

  def countOccurrences(in: Iterator[String]) =
    in.foldLeft(Map.empty[String, Long]) { (acc, s) =>
      acc + (s -> acc.get(s).map(_ + 1).getOrElse(1))
    }

  def wordsIn(file: File)(implicit codec: Codec) = for {
    line <- Source.fromFile(file).getLines
    word <- line.split("\\b")
    if word matches "[a-zA-Z0-9]+"
  } yield word

  def countWordsOccurrencesIn(file: File)(implicit codec: Codec): Map[String, Long] =
    countOccurrences(wordsIn(file))

}