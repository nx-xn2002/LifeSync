import React, {useContext} from "react";
import {
    Text,
    Heading,
    Icon,
    Button,
    CloseIcon,
    ButtonText,
    AlertDialog,
    AlertDialogBackdrop,
    AlertDialogContent,
    AlertDialogHeader,
    AlertDialogCloseButton,
    AlertDialogBody,
    AlertDialogFooter,
} from "./ui";
import {AuthContext} from "@/context/AuthContext";

const LogoutAlertDialog = ({
                               openLogoutAlertDialog,
                               setOpenLogoutAlertDialog,
                               action,
                               navigation,
                           }: any) => {
    const handleClose = () => {
        setOpenLogoutAlertDialog(false);
    };
    const {user, storeUser} = useContext(AuthContext);
    const userLogout = () => {
        user.username = ''
        user.password = ''
        storeUser(user);
        setOpenLogoutAlertDialog(false);
        navigation.navigate('Login');
    }
    return (
        <AlertDialog isOpen={openLogoutAlertDialog} onClose={handleClose}>
            <AlertDialogBackdrop/>
            <AlertDialogContent className="p-4">
                <AlertDialogHeader>
                    <Heading>{action}</Heading>
                    <AlertDialogCloseButton>
                        <Icon as={CloseIcon}/>
                    </AlertDialogCloseButton>
                </AlertDialogHeader>
                <AlertDialogBody className="" contentContainerClassName="">
                    <Text className="mb-6">Are you sure, you want to {action}?</Text>
                </AlertDialogBody>
                <AlertDialogFooter>
                    <Button variant="outline" action="secondary" onPress={handleClose}>
                        <ButtonText>Cancel</ButtonText>
                    </Button>
                    <Button action="negative" onPress={userLogout}>
                        <ButtonText className="text-white">{action}</ButtonText>
                    </Button>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    );
};

export default LogoutAlertDialog;