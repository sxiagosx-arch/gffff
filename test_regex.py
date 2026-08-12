import re

url = "http://on7.online/get.php?username=309885043&password=065369135&type=m3u_plus&output=ts"
pattern = r"(https?://[^/]+)/get\.php\?.*username=([^&]+).*&password=([^&]+)"
# Actually better to just parse url query params or a looser regex
m = re.match(r"(https?://[^/]+)/get\.php.*username=([^&]+).*password=([^&]+)", url)
if m:
    print(m.groups())
