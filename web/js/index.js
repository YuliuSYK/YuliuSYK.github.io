// 定义一组预设的签名
const signatures = [
    "或许长夜仍未终结，但希望总闪着金色的光。",
    "只要不失去你的崇高，整个世界都会向你敞开。",
    "一切有为法，如梦幻泡影，如露亦如电，应作如是观。",
    "雁渡寒潭，雁过而潭不留影；风来疏竹，风过而竹不留声。",
    "温柔正确的人总是难以生存，因为这个世界既不温柔也不正确。",
    "天空湛蓝晴朗，微风轻声歌唱，河水潺潺流淌，我的心充满希望。",
    "请你务必，一而再，再而三，三而不竭，千次万次，毫不犹豫地救自己于这世间水火。",
    "是啊，生命因何而沉睡...我们仍未知晓这一问的答案，却已然要从这场梦中清醒。又或者，这就是答案本身。",
    "我们做了可以做的一切，然后将希望留给未来。时间会弥合伤口，爱意会抚平疤痕。漫漫长夜后，罗德岛会迎来黎明。",
    "我们的幸福将属于千百万人，我们的事业将悄然无声地存在下去，但是它会永远发挥作用，而面对我们的骨灰，高尚的人们将洒下热泪。",
    "Seul un adieu fleurira.",
    "Do not go gentle into that good night.",
    "Great ideals but through selfless struggle and sacrifice to achieve.",
    "To the thirsty I will give water without cost from the spring of the water of life.",
    "When you have eliminated the impossibles,whatever remains,however improbable,must be the truth."
];

// 获取当前显示的签名索引和随机序列
let currentIndex = localStorage.getItem('currentIndex');
let randomOrder = localStorage.getItem('randomOrder');

if (currentIndex === null) {
    // 初始值，表示从第一个签名开始
    currentIndex = 0;
} else {
    currentIndex = parseInt(currentIndex);
}

if (randomOrder === null) {
    // 初始值，生成一个随机排列的序列
    randomOrder = generateRandomOrder(signatures.length);
} else {
    randomOrder = JSON.parse(randomOrder);
}

// 更新页面上的签名内容
function updateSignature() {
    const signatureIndex = randomOrder[currentIndex];
    document.getElementById('signature').textContent = signatures[signatureIndex];
    // 保存当前签名索引到本地存储
    localStorage.setItem('currentIndex', currentIndex);
    localStorage.setItem('randomOrder', JSON.stringify(randomOrder));
}

// 页面加载完成后更新签名
document.addEventListener('DOMContentLoaded', updateSignature);

// 为刷新签名按钮添加点击事件
document.getElementById('refreshSignature').addEventListener('click', function () {
    // 更新到下一个签名
    currentIndex = (currentIndex + 1) % randomOrder.length;
    if (currentIndex === 0) {
        // 如果到达序列末尾，生成新的随机序列
        randomOrder = generateRandomOrder(signatures.length);
        localStorage.setItem('randomOrder', JSON.stringify(randomOrder));
    }
    updateSignature();
});

// 生成一个随机排列的序列
function generateRandomOrder(length) {
    const order = Array.from({length}, (_, i) => i);
    for (let i = order.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [order[i], order[j]] = [order[j], order[i]];
    }
    return order;
}