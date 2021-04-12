package dev.eastar.moneykeypad

val CharSequence.parseMoney get() = replace("[^\\d-+.]".toRegex(), "").toLongOrNull() ?: 0L

val CharSequence.own get() = parseMoney.own
val Number.own get() = "%,d".format(this)

val CharSequence.own_ get() = parseMoney.own_
val Number.own_ get() = "%,d원".format(this)