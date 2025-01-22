import React, { useState, useRef } from 'react';
import { BackHandler } from 'react-native';
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
} from './ui';

const ExitAppDialog = () => {
    const [openExitDialog, setOpenExitDialog] = useState(false);
    const cancelRef = useRef(null);

    const handleExit = () => {
        BackHandler.exitApp(); // 退出应用
    };

    const handleCancel = () => {
        setOpenExitDialog(false);
    };

    React.useEffect(() => {
        const backAction = () => {
            setOpenExitDialog(true);
            return true; // 阻止默认行为
        };

        const backHandler = BackHandler.addEventListener('hardwareBackPress', backAction);
        return () => backHandler.remove();
    }, []);

    return (
        <>
            <AlertDialog
                isOpen={openExitDialog}
                onClose={handleCancel}
                leastDestructiveRef={cancelRef}
            >
                <AlertDialogBackdrop />
                <AlertDialogContent className="p-4">
                    <AlertDialogHeader>
                        <Heading>Exit App</Heading>
                        <AlertDialogCloseButton>
                            <Icon as={CloseIcon} />
                        </AlertDialogCloseButton>
                    </AlertDialogHeader>
                    <AlertDialogBody>
                        <Text className="mb-6">Do you want to exit the app?</Text>
                    </AlertDialogBody>
                    <AlertDialogFooter>
                        <Button
                            variant="outline"
                            action="secondary"
                            onPress={handleCancel}
                        >
                            <ButtonText>Cancel</ButtonText>
                        </Button>
                        <Button action="negative" onPress={handleExit}>
                            <ButtonText className="text-white">Yes</ButtonText>
                        </Button>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </>
    );
};

export default ExitAppDialog;
