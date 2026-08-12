import re

with open('app/src/main/java/com/example/network/IPTVRepository.kt', 'r') as f:
    content = f.read()

target = """    suspend fun getActiveAccount(): PlaylistAccount? = withContext(Dispatchers.IO) {
        dao.getActiveAccount()
    }"""
replace = """    suspend fun getActiveAccount(): PlaylistAccount? = withContext(Dispatchers.IO) {
        dao.getActiveAccount()
    }
    
    suspend fun getAccountExpiration(): String = withContext(Dispatchers.IO) {
        val active = dao.getActiveAccount() ?: return@withContext "Nenhuma conta"
        if (active.type == "XTREAM") {
            try {
                val url = "${active.serverUrl}/player_api.php?username=${active.username}&password=${active.password}"
                val jsonStr = getNetworkString(url)
                if (jsonStr.isEmpty()) return@withContext "Falha na conexão"
                val root = org.json.JSONObject(jsonStr)
                val userInfo = root.optJSONObject("user_info")
                if (userInfo != null) {
                    val exp = userInfo.optString("exp_date", "")
                    if (exp.isNotEmpty() && exp != "null") {
                        val expLong = exp.toLongOrNull()
                        if (expLong != null) {
                            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                            return@withContext sdf.format(java.util.Date(expLong * 1000L))
                        }
                        return@withContext exp
                    }
                    return@withContext "Ilimitado"
                }
            } catch(e: Exception) {
                return@withContext "Desconhecido"
            }
        }
        return@withContext "Não aplicável (M3U)"
    }"""

content = content.replace(target, replace)

with open('app/src/main/java/com/example/network/IPTVRepository.kt', 'w') as f:
    f.write(content)
