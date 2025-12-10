import { baseURL } from '@/api/request.js'

let socketTask = null
let isConnected = false
let reconnectTimer = null
let messageCallback = null
let userId = null

// 获取WebSocket地址
function getWsUrl(uid) {
    // 将http/https替换为ws/wss
    let url = baseURL.replace('http', 'ws')
    if (url.endsWith('/')) {
        url = url.slice(0, -1)
    }
    return `${url}/ws/${uid}`
}

export const websocket = {
    // 连接
    connect(uid) {
        if (isConnected || socketTask) return
        userId = uid

        const url = getWsUrl(uid)
        console.log('Connecting to WebSocket:', url)

        socketTask = uni.connectSocket({
            url: url,
            success: () => {
                console.log('WebSocket connect success')
            },
            fail: (err) => {
                console.error('WebSocket connect failed', err)
            }
        })

        socketTask.onOpen(() => {
            console.log('WebSocket opened')
            isConnected = true
            // 清除重连定时器
            if (reconnectTimer) {
                clearTimeout(reconnectTimer)
                reconnectTimer = null
            }
        })

        socketTask.onMessage((res) => {
            console.log('WebSocket received:', res.data)
            try {
                const data = JSON.parse(res.data)
                // 使用全局事件总线分发消息
                uni.$emit('websocket-message', data)

                // 兼容旧的回调方式 (如果还有地方用的话)
                if (messageCallback) {
                    messageCallback(data)
                }
            } catch (e) {
                console.error('Parse message failed', e)
            }
        })

        socketTask.onClose(() => {
            console.log('WebSocket closed')
            isConnected = false
            socketTask = null
            // 尝试重连
            if (userId) {
                reconnectTimer = setTimeout(() => {
                    this.connect(userId)
                }, 3000)
            }
        })

        socketTask.onError((err) => {
            console.error('WebSocket error', err)
            isConnected = false
        })
    },

    // 断开连接
    disconnect() {
        userId = null
        if (socketTask) {
            socketTask.close()
            socketTask = null
        }
        isConnected = false
        if (reconnectTimer) {
            clearTimeout(reconnectTimer)
            reconnectTimer = null
        }
    },

    // 注册消息回调
    onMessage(callback) {
        messageCallback = callback
    },

    // 发送消息 (虽然我们主要用API发送，但保留此接口)
    send(data) {
        if (socketTask && isConnected) {
            socketTask.send({
                data: typeof data === 'object' ? JSON.stringify(data) : data
            })
        }
    }
}
