<!-- 省市区选择组件 -->
<template>
  <el-cascader
    clearable
    :value="value"
    :options="regionsData"
    :placeholder="placeholder"
    popper-class="ele-pop-wrap-higher"
    :props="props"
    @input="updateValue"
  />
</template>

<script>
  import { getRegionsData } from './load-data';

  export default {
    name: 'RegionsSelect',
    props: {
      value: Array,
      placeholder: String,
      options: Array,
      valueField: {
        type: String,
        validator: (val) => {
          return !val || val === 'label';
        }
      },
      type: {
        type: String,
        validator: (type) => {
          return !type || ['provinceCity', 'province'].includes(type);
        }
      },
      props: Object
    },
    data() {
      return {
        // 级联选择器数据
        regionsData: []
      };
    },
    methods: {
      /* 更新 value */
      updateValue(value) {
        this.$emit('input', value);
      },
      /* 级联选择器数据 value 处理 */
      formatData(data) {
        if (this.valueField === 'label') {
          return data.map((d) => {
            const item = {
              label: d.label,
              value: d.label
            };
            if (d.children) {
              item.children = d.children.map((c) => {
                const cItem = {
                  label: c.label,
                  value: c.label
                };
                if (c.children) {
                  cItem.children = c.children.map((cc) => {
                    return {
                      label: cc.label,
                      value: cc.label
                    };
                  });
                }
                return cItem;
              });
            }
            return item;
          });
        } else {
          return data;
        }
      },
      /* 省市区数据筛选 */
      filterData(data) {
        if (this.type === 'provinceCity') {
          return this.formatData(
            data.map((d) => {
              const item = {
                label: d.label,
                value: d.value
              };
              if (d.children) {
                item.children = d.children.map((c) => {
                  return {
                    label: c.label,
                    value: c.value
                  };
                });
              }
              return item;
            })
          );
        } else if (this.type === 'province') {
          return this.formatData(
            data.map((d) => {
              return {
                label: d.label,
                value: d.value
              };
            })
          );
        } else {
          return this.formatData(data);
        }
      }
    },
    watch: {
      options: {
        handler(options) {
          this.regionsData = this.filterData(options ?? []);
          if (!options) {
            getRegionsData().then((data) => {
              this.regionsData = this.filterData(data ?? []);
              this.$emit('load-data-done', data);
            });
          }
        },
        immediate: true
      }
    }
  };
</script>
