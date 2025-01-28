import {StatusBar, StyleSheet, Text, TouchableOpacity, View} from "react-native";
import type {BottomTabNavigationHelpers} from "@react-navigation/bottom-tabs/src/types";
import {Button, ButtonText} from "@/components/ui";
import {useState} from "react";
import {CameraType, CameraView, useCameraPermissions} from "expo-camera";

export default function MonitorScreen({navigation}: { navigation: BottomTabNavigationHelpers }) {
    const [facing, setFacing] = useState<CameraType>('back');
    const [permission, requestPermission] = useCameraPermissions();
    if (!permission) {
        // Camera permissions are still loading.
        return <View/>;
    }
    if (!permission.granted) {
        // Camera permissions are not granted yet.
        return (
            <View style={styles.container}>
                <Text style={styles.message}>We need your permission to show the camera</Text>
                <Button onPress={requestPermission}>
                    <ButtonText>
                        Grant Permission
                    </ButtonText>
                </Button>
            </View>
        );
    }

    function toggleCameraFacing() {
        setFacing(current => (current === 'back' ? 'front' : 'back'));
    }

    return (
        <View style={styles.container}>
            <Text style={styles.text}>Monitor Screen</Text>
            <CameraView style={styles.camera} facing={facing}>
                <View style={styles.buttonContainer}>
                    <TouchableOpacity style={styles.button} onPress={toggleCameraFacing}>
                        <Text style={styles.text}>Flip Camera</Text>
                    </TouchableOpacity>
                </View>
            </CameraView>
        </View>
    );
}
const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        paddingTop: StatusBar.currentHeight,
    }, text: {
        fontSize: 20,
        fontWeight: 'bold',
        marginBottom: 20,
    }, message: {
        textAlign: 'center',
        paddingBottom: 10,
    }, camera: {
        flex: 1,
    }, buttonContainer: {
        flex: 1,
        flexDirection: 'row',
        backgroundColor: 'transparent',
        margin: 64,
    },
    button: {
        flex: 1,
        alignSelf: 'flex-end',
        alignItems: 'center',
    },
});