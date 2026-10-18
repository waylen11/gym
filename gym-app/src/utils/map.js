// 浏览器原生定位工具（签到用）
// 注意：浏览器要求 https 或 localhost 才允许定位，本地调试用 localhost:5173
export function getLocation() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      return reject(new Error('浏览器不支持定位'))
    }
    navigator.geolocation.getCurrentPosition(
      (pos) => resolve({ lat: pos.coords.latitude, lng: pos.coords.longitude }),
      () => reject(new Error('定位失败，请开启定位权限')),
      { enableHighAccuracy: true, timeout: 8000 }
    )
  })
}
