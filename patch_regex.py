import re

with open('/app/applet/app/src/main/java/com/example/network/IPTVRepository.kt', 'r') as f:
    content = f.read()

target = """            val regex = ".*S(\\\\d+)E(\\\\d+).*".toRegex(RegexOption.IGNORE_CASE)
            val regex2 = ".*S(\\\\d+).*".toRegex(RegexOption.IGNORE_CASE)"""
replace = """            val regex = "S\\\\d+[E|e]\\\\d+".toRegex(RegexOption.IGNORE_CASE)
            val regex2 = "S\\\\d+".toRegex(RegexOption.IGNORE_CASE)
            val seasonEpMatch = ".*S(\\\\d+)E(\\\\d+).*".toRegex(RegexOption.IGNORE_CASE)
            val seasonMatch = ".*S(\\\\d+).*".toRegex(RegexOption.IGNORE_CASE)"""
content = content.replace(target, replace)

target2 = """                val match = regex.find(ep.name)
                if (match != null) {
                    sNum = match.groupValues[1].toIntOrNull() ?: 1
                } else {
                    val match2 = regex2.find(ep.name)
                    if (match2 != null) {
                        sNum = match2.groupValues[1].toIntOrNull() ?: 1
                    }
                }"""
replace2 = """                val match = seasonEpMatch.find(ep.name)
                if (match != null) {
                    sNum = match.groupValues[1].toIntOrNull() ?: 1
                } else {
                    val match2 = seasonMatch.find(ep.name)
                    if (match2 != null) {
                        sNum = match2.groupValues[1].toIntOrNull() ?: 1
                    }
                }"""
content = content.replace(target2, replace2)

with open('/app/applet/app/src/main/java/com/example/network/IPTVRepository.kt', 'w') as f:
    f.write(content)
