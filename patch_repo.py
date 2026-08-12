import re

with open('/app/applet/app/src/main/java/com/example/network/IPTVRepository.kt', 'r') as f:
    content = f.read()

target = """                seasonsMap.getOrPut(sNum) { mutableListOf() }.add(ep)
            }
            
            val result = mutableListOf<IPTVSeason>()"""
replace = """                var cleanName = ep.name
                val cleanMatch = regex.find(ep.name)
                if (cleanMatch != null) {
                    cleanName = ep.name.substring(cleanMatch.range.last + 1).trim().removePrefix("-").trim()
                } else {
                    val cleanMatch2 = regex2.find(ep.name)
                    if (cleanMatch2 != null) {
                        cleanName = ep.name.substring(cleanMatch2.range.last + 1).trim().removePrefix("-").trim()
                    }
                }
                if (cleanName.isEmpty() || cleanName.equals(ep.name, ignoreCase = true)) {
                    // Try another common pattern: Title S01 E01 - EpName
                    val fallbackRegex = "S\\\\d+\\\\s*E\\\\d+(.*)".toRegex(RegexOption.IGNORE_CASE)
                    val fb = fallbackRegex.find(ep.name)
                    if (fb != null && fb.groupValues.size > 1) {
                        cleanName = fb.groupValues[1].trim().removePrefix("-").trim()
                    }
                }
                if (cleanName.isEmpty()) {
                    cleanName = "Episódio " + (seasonsMap[sNum]?.size?.plus(1) ?: 1)
                }
                
                val cleanEp = ep.copy(name = cleanName)
                seasonsMap.getOrPut(sNum) { mutableListOf() }.add(cleanEp)
            }
            
            val result = mutableListOf<IPTVSeason>()"""
content = content.replace(target, replace)

with open('/app/applet/app/src/main/java/com/example/network/IPTVRepository.kt', 'w') as f:
    f.write(content)
