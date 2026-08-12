import re

def add_keys_to_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # match items(list) { ... } -> items(list, key = { it.id }) { ... }
    # be careful not to replace if already has key or list doesn't have id
    content = re.sub(r'items\((filteredChannels)\) { (ch) ->', r'items(\1, key = { it.id }) { \2 ->', content)
    content = re.sub(r'items\((liveCategories)\) { (cat) ->', r'items(\1, key = { it.id }) { \2 ->', content)
    content = re.sub(r'items\((moviesCategories)\) { (cat) ->', r'items(\1, key = { it.id }) { \2 ->', content)
    content = re.sub(r'items\((seriesCategories)\) { (cat) ->', r'items(\1, key = { it.id }) { \2 ->', content)
    content = re.sub(r'items\((filteredMovies)\) { (movie) ->', r'items(\1, key = { it.id }) { \2 ->', content)
    content = re.sub(r'items\((filteredSeries)\) { (series) ->', r'items(\1, key = { it.id }) { \2 ->', content)
    
    with open(filepath, 'w') as f:
        f.write(content)

for file in [
    'app/src/main/java/com/example/ui/screens/LiveTVScreen.kt',
    'app/src/main/java/com/example/ui/screens/MoviesScreen.kt',
    'app/src/main/java/com/example/ui/screens/SeriesScreen.kt'
]:
    add_keys_to_file(file)

