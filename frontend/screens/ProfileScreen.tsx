import {SafeAreaView, StatusBar, StyleSheet, View} from "react-native";
import {useContext, useEffect, useState} from "react";

import type {BottomTabNavigationHelpers} from "@react-navigation/bottom-tabs/src/types";
import {AuthContext} from "@/context/AuthContext";
import LogoutAlertDialog from "@/components/LogoutAlertDialog";
import {Button, ButtonText, Divider, Heading, HStack, Icon, Pressable, Text, VStack,} from "@/components/ui";
import AntDesign from '@expo/vector-icons/AntDesign';
import FontAwesome6 from '@expo/vector-icons/FontAwesome6';
import md5 from "md5"
import BasicHealthModal from "@/components/BasicHealthModal";

export default function ProfileScreen({navigation}: { navigation: BottomTabNavigationHelpers }) {
    const {user, storeUser} = useContext(AuthContext);
    const [openLogoutAlertDialog, setOpenLogoutAlertDialog] = useState(false);
    const [modalVisible, setModalVisible] = useState(false);
    useEffect(() => {
        console.log(user)
        if (!user) {

        }
    }, []);
    return (
        //TODO:添加用户基本信息显示
        <SafeAreaView style={styles.container}>
            <VStack className="px-5 py-4 flex-1" space="lg">
                <Heading className="mb-1">Profile</Heading>
                <ProfileCard user={user}/>
                <Divider className="my-2"/>
                <PersonalInfoSection setModalVisible={setModalVisible}/>
                <Divider className="my-2"/>
                <SupportSection/>
                <Divider className="my-2"/>
                <LogoutButton
                    openLogoutAlertDialog={openLogoutAlertDialog}
                    setOpenLogoutAlertDialog={setOpenLogoutAlertDialog}
                    action={user.username !== '' ? "切换账号" : "登录"}
                />
            </VStack>
            <LogoutAlertDialog
                setOpenLogoutAlertDialog={setOpenLogoutAlertDialog}
                openLogoutAlertDialog={openLogoutAlertDialog}
                action={user.username !== '' ? "切换账号" : "登录"}
                navigation={navigation}
            />
            <BasicHealthModal
                modalVisible={modalVisible}
                setModalVisible={setModalVisible}
            />
        </SafeAreaView>
    );
}
const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f5f5f5',
        paddingTop: StatusBar.currentHeight,
    }, infoCard: {
        backgroundColor: 'white',
        borderRadius: 16,
        borderWidth: 1.5,
        padding: 16,
        margin: 16,
        elevation: 35,
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginBottom: 32
    }, title: {}, body: {
        color: 'gray'
    },
});
const generateColorFromUsername = (username: string) => {
    // 使用 MD5 哈希算法来生成用户名的哈希值
    const hash = md5(username);
    // 获取哈希值前六位，并转化为颜色
    return `#${hash.substring(0, 6)}`;
};
const ProfileCard = ({user}: any) => {
    const username = user.username === '' ? '游客账号' : user.username;
    const randomColor = generateColorFromUsername(username); // 根据用户名生成固定颜色
    const initials = username.split(' ').map((word: string) => word[0]).join(''); // 获取首字母
    return (
        <HStack className="justify-between items-center">
            <HStack space="md">
                <View
                    style={{
                        backgroundColor: randomColor,
                        width: 40,
                        height: 40,
                        borderRadius: 50,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                    }}
                >
                    <Text style={{color: 'white', fontWeight: 'bold'}}>
                        {initials}
                    </Text>
                </View>
                <VStack>
                    <Text>{user.username === '' ? '游客账号' : user.username}</Text>
                </VStack>
            </HStack>
        </HStack>
    );
};

const PersonalInfoSection = ({setModalVisible}: any) => {
    return (
        <VStack space="lg">
            <Pressable onPress={() => setModalVisible(true)}>
                <HStack className="justify-between">
                    <HStack space="md">
                        <Icon as={() => <AntDesign name="user" size={24} color="black"/>}/>
                        <Text>Personal Info</Text>
                    </HStack>
                    <Icon as={() => <AntDesign name="right" size={24} color="black"/>}/>
                </HStack>
            </Pressable>
            <Pressable onPress={() => setModalVisible(true)}>
                <HStack className="justify-between">
                    <HStack space="md">
                        <Icon as={() => <AntDesign name="setting" size={24} color="black"/>}/>
                        <Text>Account</Text>
                    </HStack>
                    <Icon as={() => <AntDesign name="right" size={24} color="black"/>}/>
                </HStack>
            </Pressable>
        </VStack>
    );
};

const SupportSection = () => {
    return (
        <VStack space="lg">
            <Heading className="mb-1">Support</Heading>
            <Pressable disabled={true}>
                <HStack className="justify-between">
                    <HStack space="md">
                        <Icon as={() => <AntDesign name="questioncircleo" size={24} color="black"/>}/>
                        <Text>Get Help</Text>
                    </HStack>
                    <Icon as={() => <AntDesign name="right" size={24} color="black"/>}/>
                </HStack>
            </Pressable>
            <Pressable disabled={true}>
                <HStack className="justify-between">
                    <HStack space="md">
                        <Icon as={() => <FontAwesome6 name="headset" size={24} color="black"/>}/>
                        <Text>Contact Support</Text>
                    </HStack>
                    <Icon as={() => <AntDesign name="right" size={24} color="black"/>}/>
                </HStack>
            </Pressable>
        </VStack>
    );
};
const LogoutButton = ({setOpenLogoutAlertDialog, action}: any) => {
    return (
        <Button
            action="secondary"
            variant="outline"
            onPress={() => {
                setOpenLogoutAlertDialog(true);
            }}

        >
            <ButtonText>{action}</ButtonText>
        </Button>
    );
};