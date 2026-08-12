import base64
# 1x1 transparent PNG
png_base64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII="
with open("app/src/main/res/drawable/logo_img.png", "wb") as f:
    f.write(base64.b64decode(png_base64))
