import { Dimensions, StatusBar, StyleSheet, Text, View } from "react-native";
import type { BottomTabNavigationHelpers } from "@react-navigation/bottom-tabs/src/types";
import { Button, ButtonText } from "@/components/ui";
import { useEffect, useRef, useState } from "react";
import { CameraView, useCameraPermissions } from "expo-camera";
import { analyse } from "@/services/api";

export default function MonitorScreen({ navigation }: { navigation: BottomTabNavigationHelpers }) {
    const [permission, requestPermission] = useCameraPermissions();
    const [countdown, setCountdown] = useState<number | null>(null); // 初始倒计时
    const [isRecording, setIsRecording] = useState(false);
    const [pictures, setPictures] = useState<string[]>([]); // 用于保存拍摄的图片
    const [recordingTime, setRecordingTime] = useState(10); // 录制倒计时（10秒）
    const cameraRef = useRef<CameraView>(null);

    // 倒计时并开始拍照
    const startRecordingWithCountdown = async () => {
        if (!permission.granted) {
            // 如果没有权限，请求权限
            const { status } = await requestPermission();
            if (status !== 'granted') {
                alert("Permission to access the camera is required!");
                return; // 如果用户拒绝权限，则不继续执行
            }
        }

        // 权限获取后，开始倒计时
        setCountdown(3); // 设置倒计时为 3 秒

        const interval = setInterval(() => {
            setCountdown(prev => {
                if (prev === 1) {
                    clearInterval(interval);
                    setCountdown(null);
                    startRecording(); // 计时结束后开始拍照
                }
                return prev ? prev - 1 : null;
            });
        }, 1000);
    };

    // 开始拍照
    const startRecording = async () => {
        console.log("准备开始拍照");
        if (!cameraRef.current) return;
        setIsRecording(true);
        setPictures([]); // 清空之前的图片列表
        setRecordingTime(30); // 初始化录制倒计时为 30 秒

        // 按时间间隔拍摄照片（持续 30 秒）
        const totalDuration = 30 * 1000;  // 30秒
        const intervalTime = 100; // 0.1 秒
        const intervalCount = totalDuration / intervalTime;

        let capturedPictures: string[] = [];
        let lastUpdateTime = 0; // 用于倒计时更新的时间控制
        const updateInterval = 500; // 500ms 更新一次倒计时

        console.log("正在开始拍照");
        for (let i = 0; i < intervalCount; i++) {
            if (cameraRef.current) {
                try {
                    const picture = await cameraRef.current.takePictureAsync({
                        quality: 0.5, // 控制图片质量，范围 0.0 - 1.0，0.5 是中等质量
                        base64: true, // 保留 base64 格式
                    });
                    capturedPictures.push(picture.base64!); // 使用 base64 格式保存图片
                } catch (error) {
                    console.error("Picture capture failed:", error);
                    break;  // 如果捕捉失败，停止拍摄
                }
            }

            // 每隔一定时间更新倒计时
            const currentTime = Date.now();
            if (currentTime - lastUpdateTime > updateInterval) {
                setRecordingTime(prevTime => (prevTime > 0 ? prevTime - 0.1 : 0));
                lastUpdateTime = currentTime;
            }

            await new Promise(resolve => setTimeout(resolve, intervalTime));  // 每 0.1 秒拍摄一次
        }

        setPictures(capturedPictures); // 设置捕获的图片
        console.log("图片数量：", capturedPictures.length);
        setIsRecording(false);
        uploadPictures(capturedPictures);
    };

    const uploadPictures = async (capturedPictures: string[]) => {
        // 在这里实现上传逻辑
        const response = await analyse({ images: capturedPictures });
        console.log(response.data);
        console.log("message", response.message);
    };

    // 取消录制
    const stopRecording = async () => {
        setIsRecording(false);
        setPictures([]);  // 取消录制时清空图片列表
    };

    const cameraComponent =
        <CameraView ref={cameraRef} style={styles.camera} facing="back" mute={true} animateShutter={false}/>;
    if (!permission) return <View />;

    return (
        <View style={styles.container}>
            <Text style={styles.text}>Monitor Screen</Text>

            {/* 圆形相机视图 */}
            <View style={styles.cameraContainer}>
                {cameraComponent}
            </View>

            {/* 倒计时显示 */}
            {countdown !== null && (
                <Text style={styles.countdownText}>{countdown}</Text>
            )}

            {/* 录制倒计时显示 */}
            {isRecording && recordingTime > 0 && (
                <Text style={styles.recordingTimeText}>
                    Recording Time Left: {Math.floor(recordingTime)}s
                </Text>
            )}

            {/* 录制按钮 */}
            <Button onPress={startRecordingWithCountdown} disabled={isRecording || countdown !== null}>
                <ButtonText>{isRecording ? "Recording..." : "Start Recording"}</ButtonText>
            </Button>

            {/* 停止录制按钮 */}
            {isRecording && (
                <Button onPress={stopRecording}>
                    <ButtonText>Stop Recording</ButtonText>
                </Button>
            )}
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        paddingTop: StatusBar.currentHeight,
    },
    text: {
        fontSize: 20,
        fontWeight: 'bold',
        marginBottom: 20,
    },
    cameraContainer: {
        width: Dimensions.get('window').width * 0.5,
        height: Dimensions.get('window').width * 0.5,
        borderRadius: Dimensions.get('window').width * 0.5,
        overflow: 'hidden', // 关键：隐藏超出边界的部分，使其呈现圆形
        backgroundColor: 'black',
    },
    camera: {
        flex: 1,
    },
    countdownText: {
        fontSize: 40,
        fontWeight: 'bold',
        color: 'red',
        marginTop: 10,
    },
    recordingTimeText: {
        fontSize: 20,
        fontWeight: 'bold',
        color: 'green',
        marginTop: 10,
    },
});
