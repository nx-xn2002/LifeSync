import {Dimensions, StatusBar, StyleSheet, Text, View} from "react-native";
import type {BottomTabNavigationHelpers} from "@react-navigation/bottom-tabs/src/types";
import {Button, ButtonText} from "@/components/ui";
import {useState} from "react";
import {CameraType, CameraView, useCameraPermissions} from "expo-camera";

export default function MonitorScreen({navigation}: { navigation: BottomTabNavigationHelpers }) {
    const [permission, requestPermission] = useCameraPermissions();
    if (!permission) {
        return <View/>;
    }

    return (
        <View style={styles.container}>
            <Text style={styles.text}>Monitor Screen</Text>
            <View style={{
                backgroundColor: 'black',
                width: Dimensions.get('window').width * 0.5,
                height: Dimensions.get('window').width * 0.5,
                borderRadius: Dimensions.get('window').width * 0.5,
            }}>
                <CameraView style={styles.camera} facing={'back'}>
                </CameraView>
            </View>
            <Button onPress={requestPermission}>
                <ButtonText>
                    Start Camera
                </ButtonText>
            </Button>
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
    }, camera: {
        flex: 1
    },
    button: {
        flex: 1,
        alignSelf: 'flex-end',
        alignItems: 'center',
    },
});