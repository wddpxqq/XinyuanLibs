package com.originstar.access.ble

class StringUtils {
    companion object {
        /**
         * 将byte[]数组转化为String类型
         * @param arg
         * 需要转换的byte[]数组
         * @param length
         * 需要转换的数组长度
         * @return 转换后的String队形
         */
        fun toHexString(arg: ByteArray?): String? {
            var result = String()
            if (arg != null) {
                var length =arg.size
                for (i in 0 until length) {
                    result = (result
                            + if (Integer.toHexString(
                            if (arg[i] < 0) arg[i] + 256 else arg[i].toInt()
                        ).length == 1
                    ) "0"
                            + Integer.toHexString(
                        if (arg[i] < 0) arg[i] + 256 else arg[i].toInt()
                    ) else Integer.toHexString(
                        if (arg[i] < 0) arg[i] + 256 else arg[i].toInt()
                    ))
                }
                return result
            }
            return ""
        }

        /**
         * 将String转化为byte[]数组
         * @param arg
         * 需要转换的String对象
         * @return 转换后的byte[]数组
         */
        fun toByteArray(arg: String?): ByteArray? {
            if (arg != null) { /* 1.先去除String中的' '，然后将String转换为char数组 */
                val NewArray = CharArray(1000)
                val array = arg.toCharArray()
                var length = 0
                for (i in array.indices) {
                    if (array[i] != ' ') {
                        NewArray[length] = array[i]
                        length++
                    }
                }
                /* 将char数组中的值转成一个实际的十进制数组 */
                val EvenLength = if (length % 2 == 0) length else length + 1
                if (EvenLength != 0) {
                    val data = IntArray(EvenLength)
                    data[EvenLength - 1] = 0
                    for (i in 0 until length) {
                        if (NewArray[i] >= '0' && NewArray[i] <= '9') {
                            data[i] = NewArray[i] - '0'
                        } else if (NewArray[i] >= 'a' && NewArray[i] <= 'f') {
                            data[i] = NewArray[i] - 'a' + 10
                        } else if (NewArray[i] >= 'A' && NewArray[i] <= 'F') {
                            data[i] = NewArray[i] - 'A' + 10
                        }
                    }
                    /* 将 每个char的值每两个组成一个16进制数据 */
                    val byteArray = ByteArray(EvenLength / 2)
                    for (i in 0 until EvenLength / 2) {
                        byteArray[i] = (data[i * 2] * 16 + data[i * 2 + 1]).toByte()
                    }
                    return byteArray
                }
            }
            return byteArrayOf()
        }

        /**
         * 将String转化为byte[]数组
         * @param arg
         * 需要转换的String对象
         * @return 转换后的byte[]数组
         */
        fun toByteArray2(arg: String?): ByteArray? {
            if (arg != null) { /* 1.先去除String中的' '，然后将String转换为char数组 */
                val NewArray = CharArray(1000)
                val array = arg.toCharArray()
                var length = 0
                for (i in array.indices) {
                    if (array[i] != ' ') {
                        NewArray[length] = array[i]
                        length++
                    }
                }
                val byteArray = ByteArray(length)
                for (i in 0 until length) {
                    byteArray[i] = NewArray[i].toByte()
                }
                return byteArray
            }
            return byteArrayOf()
        }

        /**
         * Hex字符串转byte
         * @param inHex 待转换的Hex字符串
         * @return  转换后的byte
         */
        fun hexToByte(inHex: String): Byte {
            return inHex.toInt(16).toByte()
        }

        /**
         * hex字符串转byte数组
         * @param inHex 待转换的Hex字符串
         * @return  转换后的byte数组结果
         */
        fun hexToByteArray(inHex: String): ByteArray? {
            var inHex = inHex
            var hexlen = inHex.length
            val result: ByteArray
            if (hexlen % 2 == 1) { //奇数
                hexlen++
                result = ByteArray(hexlen / 2)
                inHex = "0$inHex"
            } else { //偶数
                result = ByteArray(hexlen / 2)
            }
            var j = 0
            var i = 0
            while (i < hexlen) {
                result[j] = hexToByte(inHex.substring(i, i + 2))
                j++
                i += 2
            }
            return result
        }
    }
}