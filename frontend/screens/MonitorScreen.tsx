import React, {useContext, useRef, useState} from "react";
import {Dimensions, StatusBar, StyleSheet, View} from "react-native";
import {
    Button,
    ButtonText,
    CloseIcon,
    Heading,
    Icon,
    Modal,
    ModalBackdrop,
    ModalBody,
    ModalCloseButton,
    ModalContent,
    ModalHeader,
    Spinner,
    Text,
} from "@/components/ui";
import {CameraView, useCameraPermissions} from "expo-camera";
import {analyse} from "@/services/api";
import {AuthContext} from "@/context/AuthContext";
import {BasicHealthContext} from "@/context/BasicHealthContext";
import colors from "tailwindcss/colors";

export default function MonitorScreen({navigation}: { navigation: any }) {
    const [permission, requestPermission] = useCameraPermissions();
    const [countdown, setCountdown] = useState<number | null>(null);
    const [isRecording, setIsRecording] = useState(false);
    const [pictures, setPictures] = useState<string[]>([]);
    const [recordingTime, setRecordingTime] = useState(10);
    const [loading, setLoading] = useState(false); // 加载状态
    const [modalVisible, setModalVisible] = useState(false); // 控制Modal显示
    const [recordData, setRecordData] = useState<USER.DetectionRecord | null>(null); // 存储返回的数据
    const cameraRef = useRef<CameraView>(null);
    const {user} = useContext(AuthContext);
    const {basicHealth} = useContext(BasicHealthContext);

    // 启动录制
    const startRecordingWithCountdown = async () => {
        if (!permission.granted) {
            const {status} = await requestPermission();
            if (status !== 'granted') {
                alert("Permission to access the camera is required!");
                return;
            }
        }

        setCountdown(3);
        const interval = setInterval(() => {
            setCountdown(prev => {
                if (prev === 1) {
                    clearInterval(interval);
                    setCountdown(null);
                    startRecording();
                }
                return prev ? prev - 1 : null;
            });
        }, 1000);
    };

    const startRecording = async () => {
        setIsRecording(true);
        setPictures([]);
        setRecordingTime(30);
        const totalDuration = 30 * 1000;
        const intervalTime = 100;
        const intervalCount = totalDuration / intervalTime;

        let capturedPictures: string[] = [];
        for (let i = 0; i < intervalCount; i++) {
            if (cameraRef.current) {
                try {
                    const picture = await cameraRef.current.takePictureAsync({
                        quality: 0.5,
                    });
                    capturedPictures.push(picture.base64);
                    picture.base64 = null;
                } catch (error) {
                    console.error("Picture capture failed:", error);
                    break;
                }
            }
            await new Promise(resolve => setTimeout(resolve, intervalTime));
            setRecordingTime(prevTime => (prevTime > 0 ? prevTime - 0.1 : 0));
        }
        setPictures(capturedPictures);
        setIsRecording(false);
        uploadPictures(capturedPictures);
    };

    const uploadPictures = async (capturedPictures: string[]) => {
        setLoading(true);
        setModalVisible(true); // 显示加载弹窗

        try {
            const response = await analyse({images: capturedPictures, username: user?.username, basicHealth});
            if (response.data) {
                setRecordData(response.data);
            } else {
                console.error(response.message || "Unknown error");
                setRecordData(null); // 如果没有数据，显示空结果
            }
        } catch (error) {
            console.error("Error uploading pictures", error);
            setRecordData(null); // 如果请求失败，显示空结果
        } finally {
            setLoading(false);
        }
    };

    const stopRecording = async () => {
        setIsRecording(false);
        setPictures([]);
    };

    const closeModal = () => {
        setModalVisible(false);
    };

    if (!permission) return <View/>;

    return (
        <View style={styles.container}>
            <Text style={styles.text}>Monitor Screen</Text>

            {/* 相机组件 */}
            <View style={styles.cameraContainer}>
                <CameraView ref={cameraRef} style={styles.camera} facing="back" mute={true} animateShutter={false}/>
            </View>

            {/* 倒计时显示 */}
            {countdown !== null && (
                <Text style={styles.countdownText}>{countdown}</Text>
            )}

            {/* 录制倒计时 */}
            {isRecording && recordingTime > 0 && (
                <Text style={styles.recordingTimeText}>Recording Time Left: {Math.floor(recordingTime)}s</Text>
            )}

            {/* 录制按钮 */}
            <Button onPress={isRecording ? stopRecording : startRecordingWithCountdown} disabled={countdown !== null}>
                <ButtonText>{isRecording ? "Recording... Click to Stop" : "Start Recording"}</ButtonText>
            </Button>

            {/* 显示结果的弹窗 */}
            <Modal isOpen={modalVisible} onClose={closeModal} size="md">
                <ModalBackdrop/>
                <ModalContent>
                    <ModalHeader>
                        <Heading size="md" className="text-typography-950">
                            Analyse Result
                        </Heading>
                        <ModalCloseButton>
                            <Icon
                                as={CloseIcon}
                                size="md"
                                className="stroke-background-400 group-[:hover]/modal-close-button:stroke-background-700 group-[:active]/modal-close-button:stroke-background-900 group-[:focus-visible]/modal-close-button:stroke-background-900"
                            />
                        </ModalCloseButton>
                    </ModalHeader>
                    <ModalBody>
                        {/* Loading Spinner */}
                        {loading ? (
                            <Spinner size="large" color={colors.gray[500]}/>
                        ) : (
                            <View>
                                {/* If no data is available */}
                                {!recordData ? (
                                    <Text>No result available. Please try again.</Text>
                                ) : (
                                    <>
                                        {recordData.systolicBp && recordData.diastolicBp && (
                                            <Text><strong>Systolic BP:</strong> {recordData.systolicBp} mmHg</Text>
                                        )}
                                        {recordData.systolicBp && recordData.diastolicBp && (
                                            <Text><strong>Diastolic BP:</strong> {recordData.diastolicBp} mmHg</Text>
                                        )}
                                        {recordData.heartRate && (
                                            <Text><strong>Heart Rate:</strong> {recordData.heartRate} bpm</Text>
                                        )}
                                        {recordData.bmi && (
                                            <Text><strong>BMI:</strong> {recordData.bmi.toFixed(2)}</Text>
                                        )}
                                        <Text style={{fontSize: 12, marginTop: 10, color: 'gray'}}>
                                            <em>Detection Time: {new Date(recordData.createTime!).toLocaleString()}</em>
                                        </Text>
                                        <Text style={{fontSize: 12, marginTop: 5, color: 'gray'}}>
                                            <em>This result is for reference only and cannot replace medical
                                                testing.</em>
                                        </Text>
                                    </>
                                )}
                            </View>
                        )}
                    </ModalBody>
                </ModalContent>
            </Modal>
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
        overflow: 'hidden',
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
