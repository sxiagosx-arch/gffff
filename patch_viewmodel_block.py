import re

with open('app/src/main/java/com/example/ui/IPTVViewModel.kt', 'r') as f:
    content = f.read()

target_block = r"fun isCategoryBlocked\(categoryId: String\): Boolean \{(.*?)\}"
replace_block = """fun isCategoryBlocked(categoryId: String, categoryName: String): Boolean {
        if (_blockAdult.value) {
            val adultWords = listOf("adult", "+18", "18+", "xxx", "porn", "sex", "erótico", "erotico", "privé", "prive")
            val isAdult = adultWords.any { word -> categoryName.contains(word, ignoreCase = true) }
            if (isAdult) return true
        }
        return _blockedItems.value.any { it.blockId == categoryId && it.type == "CATEGORY" }
    }"""

content = re.sub(target_block, replace_block, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/IPTVViewModel.kt', 'w') as f:
    f.write(content)
