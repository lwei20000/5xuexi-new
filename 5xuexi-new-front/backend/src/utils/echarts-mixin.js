/**
 * echarts 混入
 */
import store from '@/store';
import { THEME_KEY } from 'vue-echarts';
import { ChartTheme, ChartThemeDark } from 'ele-admin';

export function echartsMixin(refs) {
  return {
    provide() {
      return {
        // 主题设置
        [THEME_KEY]: this.chartsTheme
      };
    },
    data() {
      const dark = !!this.$store?.state?.theme?.darkMode;
      return {
        // 图表主题
        chartsTheme: { ...(dark ? ChartThemeDark : ChartTheme) },
        // 是否为 deactivated 状态
        deactivated: false,
        // 当前图表是否是暗黑主题
        isDark: dark
      };
    },
    computed: {
      // 内容区域宽度
      layoutContentWidth() {
        return store?.state?.theme?.contentWidth;
      },
      // 是否是暗黑主题
      darkMode() {
        return this.$store?.state?.theme?.darkMode;
      }
    },
    watch: {
      // 监听内容区域宽度变化
      layoutContentWidth() {
        this.resizeAllCharts();
      },
      // 主题跟随暗黑模式
      darkMode(dark) {
        if (!this.deactivated) {
          this.changeChartsTheme(dark);
        }
      }
    },
    // 适配 keep-alive
    activated() {
      this.deactivated = false;
      this.$nextTick(() => {
        if (this.isDark !== this.darkMode) {
          this.changeChartsTheme(this.darkMode);
        } else {
          this.resizeAllCharts();
        }
      });
    },
    deactivated() {
      this.deactivated = true;
    },
    methods: {
      /* 重置图表尺寸 */
      resizeAllCharts() {
        refs.forEach((ref) => {
          this.$refs[ref]?.resize();
        });
      },
      /* 更改图表主题 */
      changeChartsTheme(dark) {
        this.isDark = dark;
        Object.assign(this.chartsTheme, dark ? ChartThemeDark : ChartTheme);
      }
    }
  };
}
