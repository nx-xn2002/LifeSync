import React, {useContext, useState} from 'react';
import {SafeAreaView, ScrollView, StyleSheet} from 'react-native';
import {useFocusEffect} from '@react-navigation/native';
import {listDetectionRecord} from "@/services/api";
import {AuthContext} from "@/context/AuthContext";
import {Card, Heading, Spinner, Text} from '@/components/ui';
import {Badge, BadgeText} from "@/components/ui/badge";
import {Box} from "@/components/ui/box";
import {Table, TableBody, TableCaption, TableData, TableHead, TableHeader, TableRow} from '@/components/ui/table';
import colors from "tailwindcss/colors";

export default function HistoryScreen() {
    const [loading, setLoading] = useState<boolean>(true);
    const [records, setRecords] = useState<USER.DetectionRecord[]>([]);
    const [error, setError] = useState<string | null>(null);
    const {user} = useContext(AuthContext);

    // 获取历史检测记录
    const fetchRecords = async () => {
        setLoading(true);
        setError(null);

        try {
            const response = await listDetectionRecord({username: user.username}); // 假设用户名为 user123
            const allRecords = response.data || [];

            // 获取当前日期和15天前的日期
            const currentDate = new Date();
            const fifteenDaysAgo = new Date(currentDate.setDate(currentDate.getDate() - 15));

            // 过滤出最近15天的记录
            const recentRecords = allRecords.filter((record) => {
                const recordDate = new Date(record.createTime);
                return recordDate >= fifteenDaysAgo;
            });

            // 按照时间倒序排序
            recentRecords.sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime());

            setRecords(recentRecords);
        } catch (err) {
            setError('An error occurred while fetching the records.');
        } finally {
            setLoading(false);
        }
    };

    // 使用 useFocusEffect，当页面聚焦时重新加载数据
    useFocusEffect(
        React.useCallback(() => {
            fetchRecords();
        }, [])
    );

    const formatDate = (date: Date) => {
        date = new Date(date);
        return `${date.getFullYear().toString().slice(2)}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    };

    // 健康判断函数
    const getBmiStatus = (bmi: number | null) => {
        if (bmi == null) return 'info';
        if (bmi < 18.5) return 'error';  // 偏瘦
        if (bmi >= 18.5 && bmi <= 24.9) return 'success';  // 正常
        return 'warning';  // 偏胖
    };

    const getBpStatus = (systolicBp: number | null, diastolicBp: number | null) => {
        if (systolicBp == null || diastolicBp == null) return 'info';
        if (systolicBp < 120 && diastolicBp < 80) return 'success';  // 正常
        if (systolicBp >= 140 || diastolicBp >= 90) return 'error';  // 高血压
        return 'info';  // 较高但不严重
    };

    const getHeartRateStatus = (heartRate: number | null) => {
        if (heartRate == null) return 'info';
        if (heartRate >= 60 && heartRate <= 100) return 'success';  // 正常心率
        if (heartRate < 60) return 'warning';  // 低心率
        return 'error';  // 高心率
    };

    return (
        <SafeAreaView style={styles.container}>
            {loading ? (
                <Spinner size="large" color={colors.gray[500]}/>
            ) : error ? (
                <Text style={styles.errorText}>{error}</Text>
            ) : (
                <>
                    {records.length > 0 ? (
                        <Card size="md" variant="outline" className="m-3" style={styles.box}>
                            <Heading size="md" className="mb-1">
                                Records
                            </Heading>
                            <Table style={styles.table}>
                                <TableHeader>
                                    <TableRow className="border-b-0 bg-background-0 hover:bg-background-0">
                                        <TableHead style={styles.tableHead}>Date</TableHead>
                                        <TableHead style={styles.tableHead}>Diastolic BP</TableHead>
                                        <TableHead style={styles.tableHead}>Systolic BP</TableHead>
                                        <TableHead style={styles.tableHead}>Heart Rate</TableHead>
                                        <TableHead style={styles.tableHead}>BMI</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {records.map((record, index) => (
                                        <TableRow key={index} className="border-b-0 bg-background-50">
                                            <TableData style={styles.tableData}>
                                                {formatDate(record.createTime)}
                                            </TableData>

                                            <TableData style={styles.tableData}>
                                                <Badge
                                                    size="sm"
                                                    action={getBpStatus(record.systolicBp, record.diastolicBp)}
                                                    className="w-fit justify-center"
                                                >
                                                    <BadgeText>{record.diastolicBp ?? 'N/A'}</BadgeText>
                                                </Badge>
                                            </TableData>

                                            <TableData style={styles.tableData}>
                                                <Badge
                                                    size="sm"
                                                    action={getBpStatus(record.systolicBp, record.diastolicBp)}
                                                    className="w-fit justify-center"
                                                >
                                                    <BadgeText>{record.systolicBp ?? 'N/A'}</BadgeText>
                                                </Badge>
                                            </TableData>

                                            <TableData style={styles.tableData}>
                                                <Badge
                                                    size="sm"
                                                    action={getHeartRateStatus(record.heartRate)}
                                                    className="w-fit justify-center"
                                                >
                                                    <BadgeText>{record.heartRate ?? 'N/A'}</BadgeText>
                                                </Badge>
                                            </TableData>

                                            <TableData style={styles.tableData}>
                                                <Badge
                                                    size="sm"
                                                    action={getBmiStatus(record.bmi)}
                                                    className="w-fit justify-center"
                                                >
                                                    <BadgeText>{(record.bmi != null ? record.bmi.toFixed(1) : 'N/A')}</BadgeText>
                                                </Badge>
                                            </TableData>
                                        </TableRow>
                                    ))}
                                </TableBody>
                                <TableCaption>
                                    <Text>Only the last 15 days of records are displayed.</Text>
                                </TableCaption>
                            </Table>
                        </Card>
                    ) : (
                        <Text>No records available</Text>
                    )}
                </>
            )}
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 10,
    },
    errorText: {
        fontSize: 16,
        color: 'red',
    },
    scrollContainer: {
        marginTop: 20,
    },
    box: {
        alignSelf: 'center',  // 居中显示
    },
    table: {
        width: '100%',  // 确保表格占满容器宽度
    },
    tableHead: {
        fontSize: 12,
        padding: 8,
    },
    tableData: {
        fontSize: 12,
        padding: 6,
        maxWidth: 100,
    },
});
